# Resilience Roadmap

Fault-tolerance for the fuin CQRS/ES stack (`event-store-commons`, `ddd-4-java`, `cqrs-4-java`) and the
example applications, using **established patterns** — Circuit Breaker, Retry with Backoff, Bulkhead,
Timeout, Fallback — implemented with **SmallRye Fault Tolerance** on Quarkus and **Resilience4j** on
Spring Boot.

> This document is the shared design and the order of work. **What each repository actually implements is
> described in its own `resilience.md`**
> ([event-store-commons](https://github.com/fuinorg/event-store-commons/blob/develop/resilience.md),
> [ddd-4-java](https://github.com/fuinorg/ddd-4-java/blob/develop/resilience.md),
> [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java/blob/develop/resilience.md)), and the remaining open
> points in its `resilience-tasks.md`.

## Status

The design below is implemented. Phases 0 to 6 are complete apart from the two items named under
"Still open"; the descriptions in §5 and §6 are kept as the record of *why* each decision was made, and are
written as they were planned rather than as they ended up.

**Still open**

- **Metrics and health** for the breakers and bulkheads (§5.6, Phase 6). Deferred so it can be done together
  with the Micrometer instrumentation roadmap, which touches the same classes and owns the meter conventions.
- **End-to-end token validation during a Keycloak outage.** The repository-level behaviour is covered against
  a real Keycloak; putting a token through the security filter chain during an outage additionally needs
  Keycloak and OIDC wired into a test application.

**Corrections to the plan below, found while implementing**

- The example's `RemoteEntityRoleService` named in Phase 1 **no longer exists** — it was deleted, and the
  example has no command→query HTTP call left to guard. The recipe stays valid should one return.
- Phase 2's "generalize `ViewSubscriptions` to exponential backoff + jitter" is done, and the reconnect also
  exists as a store-agnostic decorator in event-store-commons.
- §5.4's single `org.fuin.cqrs4j.resilience.<scenario>.<param>` namespace was **not** adopted. Configuration
  ended up grouped by the thing being configured (`org.fuin.cqrs4j.projection.*`,
  `org.fuin.cqrs4j.pm.cmdqueue.*`), which keeps a setting next to the feature it belongs to.
- §5.2's classifier needed one correction that the plan did not anticipate: matching
  `jakarta.persistence.*` / `java.sql.*` / `org.springframework.dao.*` by prefix also captures *answers about
  the data* — optimistic-lock conflicts, constraint violations — which must never be treated as transient.

---

## 1. Why

The stack talks to four external systems, and today **none of those calls are fault-tolerant**. A grep
for `resilience4j`, `smallrye-fault-tolerance`, `microprofile-fault-tolerance` and the
`@Retry/@Timeout/@CircuitBreaker/@Bulkhead/@Fallback` annotations across all three libraries and both
examples returns **zero hits**. The only hand-rolled mechanisms are:

- an optimistic-concurrency retry loop in `ddd-4-java` (`EventStoreRepositoryAsync.attempt`) that retries
  **version conflicts only** — connectivity errors propagate raw;
- a per-call gRPC deadline + bounded retry in `esc-esgrpc` `GrpcProjectionAdminEventStore.disableProjection`
  (a good local model, but limited to one operation);
- the projection catch-up in `cqrs-4-java` view managers, which already self-heals on the schedule and
  classifies transient infra failures via `isTransientInfrastructureFailure(...)` (a seam we build on).

Everything else turns a failure into a bare `RuntimeException` and lets it propagate — a blocking
`KurrentDBClient` call with **no timeout** will hang the caller thread; an unreachable Keycloak fails
the request with no retry; the command→query HTTP call has durable redelivery but no timeout/backoff/
circuit-breaker.

## 2. Failure scenarios (what "resilience" must cover)

| # | Scenario | Where it happens (concrete code) | Blast radius today |
|---|----------|----------------------------------|--------------------|
| **S1** | **Event store not reachable** (gRPC UNAVAILABLE/DEADLINE) | `esc-esgrpc` `ESGrpcEventStore.*` (blocking `.get()`, no timeout), `ESGrpcEventStoreAsync.*`, `GrpcProjectionAdminEventStore.*`; reached from `ddd-4-java` `EventStoreRepository[Async]` and `cqrs-4-java` view managers / dispatchers | Command save/load hangs or fails raw; projections retry next tick (OK) |
| **S2** | **Database not reachable** (JDBC/JPA) | `esc-jpa` `JpaEventStore`/`AbstractJpaEventStore`; `cqrs-4-java` `Qry*` dedup/lease/position repos, read-model queries | Command dedup / projection lease fail; only `NoResultException` is handled |
| **S3** | **Keycloak not reachable** (OIDC discovery + JWKS) | Spring: `cqrs-4-java-springboot-keycloak-core` `JwtUtils.getConfiguration` (RestTemplate, 30 s), `JwtTenant.getJWSKeySelector` (Nimbus JWKS, no timeout). Quarkus: delegated to `quarkus-oidc` | First request per tenant fails; failed `computeIfAbsent` is not negatively cached (retries every request) |
| **S4** | **Command→Query HTTP call fails** | Library outbox: `cqrs-4-java` `quarkus/process-manager` `CommandRestClient` (JDK `HttpClient`, no timeout) + `springboot/process-manager` `CommandRestClient` (`RestClient`, no timeout), drained by `CommandQueueExecutor`. Example: `cqrs-keycloak-example` `RemoteEntityRoleService` → query `:8081` via `SharedExampleUtils.createRestClient` | Outbox: durable redelivery + DLQ but no timeout/backoff/CB. Example `RemoteEntityRoleService`: throws `IllegalStateException`, no resilience |
| **S5** | **Key vault not reachable** (crypto-shredding) | `ddd-4-java` `EventStoreRepositoryAsync.encryptIfRequired/decryptIfRequired` → `objects4j` `EncryptedDataService` (Vault/OpenBao behind it) | Every encrypted-event save/load aborts the whole aggregate operation on a vault hiccup |

## 3. Patterns and where each applies

| Pattern | Purpose | Primary scenarios |
|---------|---------|-------------------|
| **Timeout** | Bound every remote call so a hung dependency can't pin a thread. **Foundational — nothing else works without it.** | S1 (gRPC deadlines), S2 (JDBC statement/lock/query timeouts), S3 (HTTP), S4 (HTTP connect+read) |
| **Retry + Backoff** | Ride out brief blips; exponential backoff + jitter to avoid stampedes. Retry **only** typed-transient failures (never business errors, never non-idempotent writes without care). | S1 (reads, idempotent appends), S3 (JWKS/OIDC), S4 (outbox delivery), S5 (vault) |
| **Circuit Breaker** | Fail fast when a dependency is down; stop hammering it; auto half-open probe. | S1, S3, S4, S5 |
| **Bulkhead** | Cap concurrent calls / isolate thread pools so one slow dependency can't exhaust the app. | S1 (subscription + read pools), S4 (outbox drainer concurrency), inbound `/cmd` receiver |
| **Fallback** | Degrade gracefully: cached value, empty result, queue-for-later, or a clear typed error. | S3 (last-known JWKS), S4 (already: outbox DLQ), S5 (fail the single event, not the batch), read-side stale-OK responses |

## 4. Framework strategy (the key architectural decision)

The libraries are consumed by **both** Quarkus and Spring apps, and the failure points sit at three
depths. We split responsibilities so policy lives in the framework layer and mechanism/classification
lives in the neutral core:

### Layer A — Neutral foundation (no fault-tolerance framework)
`event-store-commons`, `ddd-4-java`, and the framework-neutral parts of `cqrs-4-java` (`core`, `esc`,
`jpa`) get **no dependency on SmallRye FT or Resilience4j**. They provide what resilience *needs* to work:

1. **Typed exceptions** — a transient/unavailable exception so downstream policies can tell "retry me"
   from "this is a business error". This is the linchpin (see §5).
2. **Timeouts at the boundary** — gRPC per-call deadlines, JDBC statement/lock/query timeouts. These are
   framework-neutral and belong at the source.
3. **Classification helpers** — reuse/extend the existing `isTransientInfrastructureFailure(Throwable)`
   pattern so both frameworks share one predicate.

Rationale: retry counts, breaker thresholds and bulkhead sizes are **deployment policy**, not library
concerns; and putting CDI/Spring-specific annotations in a plain-Java library would couple it to a
framework. (If, later, the maintainers want turnkey resilience *inside* the neutral libs, the
framework-agnostic **Resilience4j core** modules — `resilience4j-retry/-circuitbreaker/-timelimiter/-bulkhead`,
plain Java, no Spring — can decorate the esc/ddd calls programmatically. Listed as an optional path in the
per-project tasks; the default is to keep Layer A dependency-free.)

### Layer B — Quarkus integration (`cqrs-4-java-quarkus-*`, Quarkus apps) → **SmallRye Fault Tolerance**
Add `quarkus-smallrye-fault-tolerance`. Guard the CDI beans that call the neutral libraries with
MicroProfile FT annotations: `@Timeout`, `@Retry`, `@CircuitBreaker`, `@Bulkhead`, `@Fallback`
(config overridable via `<Class>/<method>/<Annotation>/<param>` MP-Config keys). Keycloak reachability
is handled by **`quarkus-oidc`** (connection-delay/retry, JWKS cache) — configured, not annotated.

### Layer C — Spring integration (`cqrs-4-java-springboot-*`, Spring apps) → **Resilience4j**
Add `io.github.resilience4j:resilience4j-spring-boot3` + `spring-boot-starter-aop`. Annotate the Spring
beans (`@Retry`, `@CircuitBreaker`, `@Bulkhead`, `@TimeLimiter`, `@RateLimiter`) or wire the
programmatic API; configure instances under `resilience4j.*` in `application.yaml`. Expose actuator
health/metrics.

### Mapping matrix

| Scenario | Neutral foundation (A) | Quarkus (B, SmallRye FT) | Spring (C, Resilience4j) |
|----------|------------------------|--------------------------|--------------------------|
| S1 event store | `EscConnectionException` + gRPC deadlines in `esc-esgrpc`; conn-retry seam in `ddd-4-java` | FT around `QuarkusCommandDispatcher` / view-manager `readAllEventsForward` | R4j around dispatcher / `SpringViewManager` reads |
| S2 database | JDBC statement/lock/query timeouts in `esc-jpa`; typed transient mapping | FT/`@Bulkhead` around dedup + lease `@Transactional` beans | R4j around dedup + lease beans; HikariCP pool sizing |
| S3 keycloak | — (HTTP is framework-owned) | `quarkus-oidc` config | R4j around `JwtUtils`/JWKS in `keycloak-core` + negative cache |
| S4 command→query HTTP | typed HTTP-failure exception; timeout on the client | FT around `CommandQueueExecutor.deliver` / `CommandRestClient.cmd` | R4j around Spring `CommandRestClient`; timeouts on `RestClient`; example `RemoteEntityRoleService` |
| S5 vault | timeout/typed exception around `EncryptedDataService` calls | FT around encrypt/decrypt seam | R4j around encrypt/decrypt seam |

## 5. Cross-cutting foundation (do this first)

1. **Typed transient exception in `esc-api`.** Add e.g. `EscConnectionException` (a `RuntimeException`
   marking "store unavailable / not reachable", distinct from the existing business exceptions
   `WrongExpectedVersionException`, `StreamNotFoundException`, …). `esc-esgrpc`
   `ESGrpcEventStoreSupport.mapException` maps `io.grpc.StatusRuntimeException` with `UNAVAILABLE` /
   `DEADLINE_EXCEEDED` / `RESOURCE_EXHAUSTED` (and `InterruptedException`) to it instead of a bare
   `RuntimeException`. **Fix the `streamExists` bug** that currently swallows a connectivity
   `StatusRuntimeException` as "stream does not exist" (`ESGrpcEventStore.streamExists`,
   `ESGrpcEventStoreAsync.streamExists`). Retry/CB predicates key off this type.
2. **A shared transient-failure classifier.** Promote the `isTransientInfrastructureFailure(Throwable)`
   logic (today duplicated in `QuarkusViewManager`/`SpringViewManager`) into a reusable helper (e.g.
   `cqrs-4-java-core` `CqrsUtils` or an esc util) so every retry/CB predicate uses one definition
   (`EscConnectionException`, `io.grpc.*`, `java.net.*`, `java.io.IOException`, `java.sql.*`,
   `jakarta.persistence.*`, `org.springframework.dao.*`).
3. **Timeouts everywhere.** gRPC per-call deadlines in `esc-esgrpc` (model on
   `GrpcProjectionAdminEventStore.disableProjection`'s `.deadline(...)`); JDBC `jakarta.persistence.query.timeout`
   + lock timeout + a pool `connection-timeout` in `esc-jpa` and the query modules; HTTP connect+read
   timeouts on both `CommandRestClient`s and the example's `RestClient`.
4. **Config & naming conventions.** One property namespace, e.g. `org.fuin.cqrs4j.resilience.<scenario>.<param>`
   for neutral defaults; SmallRye FT MP-Config keys for Quarkus; `resilience4j.*` for Spring. Document
   defaults in each module's README.
5. **Idempotency review.** Retry is only safe for idempotent operations. Reads and the effectively-once
   command outbox are safe; blind append retries are **not** (a duplicate could be written) — appends
   retry only on typed-transient failures *before* the server acknowledges, and rely on
   `ExpectedVersion` / the dedup store for exactly-once. Call this out per task.
6. **Observability.** Emit metrics for every breaker/retry/bulkhead (Micrometer is already a dep in the
   process-manager modules): retry counts, breaker state transitions, bulkhead rejections, call
   latency/timeouts. Wire actuator/health so a tripped breaker is visible.

## 6. Phased plan

*All phases are implemented except where the Status section above says otherwise. Kept as written so the
ordering and its rationale stay on record.*


- **Phase 0 — Foundation (blocking).** §5 items 1–4 in `esc-api`/`esc-esgrpc`/`esc-jpa` + the shared
  classifier in `cqrs-4-java`. No behavior change beyond typed exceptions + timeouts. Everything else
  depends on this.
- **Phase 1 — S4 command→query HTTP.** Highest value, clearest seam. Timeout + retry-with-backoff +
  circuit-breaker + bulkhead + fallback on the outbox delivery (Quarkus SmallRye FT / Spring
  Resilience4j) and on the example `RemoteEntityRoleService`. The outbox DLQ is the existing fallback;
  add fail-fast + backoff so a down query service doesn't burn the whole batch every tick.
- **Phase 2 — S1 event store.** gRPC deadlines (Phase 0) + retry/CB on reads and idempotent appends;
  subscription **auto-reconnect with backoff** in `esc-esgrpc` `subscribeToStream` (today a drop just
  surfaces an error; `cqrs-4-java` `ViewSubscriptions` already re-subscribes at a fixed 5 s — generalize
  to exponential backoff + jitter and push the reconnect into the store where appropriate). Separate the
  `ddd-4-java` connectivity-retry budget from the version-conflict budget.
- **Phase 3 — S2 database.** Statement/lock/query timeouts, retry on transient `PersistenceException`/
  `DataAccessException`, bulkhead sized to the connection pool, pool tuning.
- **Phase 4 — S3 Keycloak.** Spring: retry+timeout+CB around `JwtUtils`/JWKS, negative-cache failed
  lookups with backoff, keep a last-known-good JWKS as fallback. Quarkus: tune `quarkus-oidc`
  (`connection-delay`, `connection-retry-count`, JWKS cache TTL).
- **Phase 5 — S5 crypto/vault.** Timeout + retry + CB + fallback around `EncryptedDataService` calls so a
  vault blip fails the single event (or defers), not the whole load/save.
- **Phase 6 — Observability & fault-injection tests.** Metrics/health for all policies; integration tests
  that pause/kill the eventstore/keycloak containers (Testcontainers `pause()`/network partition or
  Toxiproxy) and assert graceful degradation + recovery.

## 7. Testing strategy

*Implemented: fault injection covers an unreachable command endpoint on both frameworks, an event store
outage with recovery, a Keycloak outage, a stalled database lock and a database that goes away. Where a
container's mapped port would change on restart, a small in-process TCP proxy injects the outage instead, so
the application keeps a stable address and recovery can be asserted.*


- **Unit**: policy config (retry counts, backoff, breaker thresholds) and the transient-vs-business
  classifier.
- **Integration (fault injection)**: with Testcontainers, `pause()`/`stop()` the eventstore, Keycloak, or
  the query service mid-test (or insert Toxiproxy for latency/blackhole) and assert: calls time out
  rather than hang; the breaker opens then half-opens; retries back off; fallbacks fire; the system
  recovers when the dependency returns. The `esc-test` TCK and the examples' ITs are the harness.
- **Load**: verify bulkheads bound concurrency and one slow dependency doesn't cascade.

## 8. Conventions

- Never retry a business exception (`WrongExpectedVersionException`, `AggregateVersionConflictException`,
  validation, 4xx). Retry only typed-transient (§5.2).
- Retry non-idempotent writes only pre-acknowledgement and behind version/dedup guards (§5.5).
- Every remote call has a timeout (§5.3) — a breaker without a timeout is a hung thread.
- Prefer configuration over code for thresholds; ship safe defaults; document them.
