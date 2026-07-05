# Demo: Backend Portability (JPA / catch-up)

| Status | Effort | Depends on |
|--------|--------|-----------|
| 📋 planned — not yet implemented | M (2–4 days) | — |

> **Scripts:** none yet. This demo is **self-contained** — it carries its own copy of the round-trip
> driver script (parameterized for the `esc-jpa` backend profile) plus its own command payloads in this
> folder; it does not depend on any other demo. See the [demos roadmap](../../demos-roadmap.md) for how
> this fits with the other demos.

## What it demonstrates

The same command and query services running against **two different event-store backends** with no
domain-code change — the KurrentDB gRPC store (`esc-esgrpc`) used today, and the relational `esc-jpa`
store (events persisted in a SQL table, projections driven by a **catch-up** poll instead of a live gRPC
subscription). This is the headline portability claim of `event-store-commons`: the domain and projection
code depend only on the `EventStore` / `SubscribableEventStore[Async]` SPI, not on a concrete store.

## Prerequisites

- `esc-jpa` present and current (it is — the command module already depends on the esc SPI; `esc-jpa` is a
  sibling module). Confirm an `esc-jpa`-backed `SubscribableEventStoreAsync` (or a catch-up view manager
  path) is wired for the query side.
- A schema for the JPA event table in the demo's MariaDB (or a separate H2/Postgres) — a Flyway/`schema.sql`
  or Hibernate `ddl-auto` for the demo.

## Approach

1. Add a Spring profile / Quarkus config profile `esc-jpa` that swaps the `@Bean`/`@Produces` for the
   event store from `IESGrpcEventStore` to the JPA-backed store, and the query view manager from the live
   subscription to the catch-up (polling) variant.
2. Point both services at the same relational store; keep the REST API identical.
3. Run this demo's own round-trip driver script against the `esc-jpa` profile — the observable behaviour
   is identical, only the store underneath changes.

## Verify

- Script: this demo's driver script with `SPRING_PROFILES_ACTIVE=esc-jpa` (or the Quarkus equivalent).
- IT: parameterize
  [`PersonE2EIT`](../../spring-boot/query/src/test/java/org/fuin/cqrs4j/example/spring/query/e2e/PersonE2EIT.java)
  (or add `PersonJpaBackendE2EIT`) to run the round trip against the JPA store, asserting the read model
  still converges.

## Open questions

Does the catch-up view manager need a distinct polling interval config for the demo? Confirm `esc-jpa`'s
subscribe semantics (poll vs. push) match what `QuarkusViewManager` / `SpringViewManager` expect.
