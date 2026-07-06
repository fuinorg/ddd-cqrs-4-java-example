# Demos Roadmap

Six end-to-end demos show off what the fuin DDD/CQRS/Event-Sourcing stack (`event-store-commons`,
`ddd-4-java`, `cqrs-4-java`, `objects4j`) can do. Each demo lives in its own subfolder under
[`demo/`](demo) with a `README.md` and the scripts it needs. The first two are **done**; the other four
are scoped so they can be picked up incrementally.

> **Requirement — demos are independent of each other.** Every demo is self-contained: it has its own
> folder, its own `README.md`, and its own copy of any scripts and payloads it needs. No demo depends on
> another demo being present or having been run first, and none reaches into another demo's folder. Shared
> command payloads are duplicated rather than referenced, so a demo can be run, changed, or removed on its
> own.

| # | Demo | Status | Effort | Depends on (library) |
|---|------|--------|--------|----------------------|
| 1 | [Cross-service command → query E2E](demo/e2e/README.md) | ✅ done | — | — |
| 2 | [Backend portability (JPA / catch-up)](demo/backend-portability/README.md) | ✅ done | M | — |
| 3 | [Crypto-shredding (DDD-4)](demo/crypto-shredding/README.md) | 📋 planned | L | objects4j / ddd-4-java feature |
| 4 | [Rolling-deploy event versioning](demo/event-versioning/README.md) | 📋 planned | M | — |
| 5 | [Projection high-availability](demo/projection-ha/README.md) | 📋 planned | L | cqrs-4-java lease work |
| 6 | [Observability](demo/observability/README.md) | 📋 planned | S–M | cqrs-4-java metrics hook |

Effort key: **S** ≈ ½–1 day, **M** ≈ 2–4 days, **L** ≈ 1 week+.

Each demo's `README.md` states **what it demonstrates**, the **prerequisites** it needs from the
libraries, a sketched **approach**, and how it is **verified** (a script + an automated IT, mirroring
demo #1's "both" form).

## Shipped: demo #1

The only implemented demo so far — the full `command aggregate → event store → query projection → query
REST` round trip, across the two microservices, including mixing the Quarkus and Spring Boot stacks:

- Scripts + JSON in [`demo/e2e/`](demo/e2e): `run-e2e-demo.sh` (orchestrator), `query-persons.sh`,
  `query-statistics.sh`, `create-persons.sh`, `delete-harry-osborn.sh`.
- Automated IT:
  [`PersonE2EIT`](spring-boot/query/src/test/java/org/fuin/cqrs4j/example/spring/query/e2e/PersonE2EIT.java).
- Walkthrough: [`demo/e2e/README.md`](demo/e2e/README.md).

## Suggested order

1. ~~**#2 backend portability**~~ — ✅ done. Closed a real library gap along the way: the `esc-jpa`
   store now serves projections as a type filter over the event log (a `ProjectionAdminEventStore` plus
   catch-up read), so the cqrs-4-java `SpringViewManager` works unchanged over the relational backend.
2. **#4 event versioning** — mostly example-level once the `esc` metadata seam is confirmed.
3. **#6 observability (metrics + health first)** — low effort, mostly configuration.
4. **#5 projection-HA** and **#3 crypto-shredding** — both likely need library features first
   (lease; encrypting serializer). Scope the library task, then the demo.
