# Demo: Backend Portability (JPA / catch-up)

> **Self-contained.** This demo carries its own copy of the round-trip driver script
> ([`run-backend-portability-demo.sh`](run-backend-portability-demo.sh)), the two query helpers
> and its command payloads; it does not depend on any other demo.

## What it demonstrates

The **same** command and query services running against **two different event-store backends** with
no domain-code change — the KurrentDB gRPC store (`esc-esgrpc`) used by [demo #1](../e2e/README.md),
and the relational **`esc-jpa`** store, where events are persisted in a SQL database and the query
projection is driven by a **catch-up poll** instead of a live gRPC subscription.

This is the headline portability claim of `event-store-commons`: the aggregate, the event-sourced
repository and the projection views depend only on the `EventStore` SPI, not on any concrete store.
Swapping the backend is a matter of activating a Spring profile.

```
   command REST (:8081)  --POST/DELETE-->  ┌──────────────────┐  --poll/catch-up-->  query projection  --JPA-->  query REST (:8080)
                                           │  event store     │
   Person aggregate --appends events-->    │  (relational SQL)│  --PersonCreatedEvent / PersonDeletedEvent-->  read model
                                           └──────────────────┘
```

Behind the `EventStore` SPI, a projection on the relational backend is a **type filter over the
event log**: the store selects the events of the types a view is interested in, in their global
order, and the query side's `SpringViewManager` catches up over them by polling. The command service
keeps appending to per-aggregate `PERSON-<id>` streams, unaware that a projection exists.

## What changes vs. demo #1

Nothing in the domain, the projection views or the REST layer. Only wiring, all behind the
`esc-jpa` Spring profile:

- the event-store bean is the relational `esc-jpa` store instead of the KurrentDB gRPC store;
- the projection runs in **poll** mode (`org.fuin.cqrs4j.projection.mode=poll`) — the relational
  store has no live subscription;
- both services point at the same relational database.

Everything else — the `Person` aggregate, `EventStorePersonRepository`, `PersonListView`,
`StatisticViewEvents` / `StatisticViewCategories`, and the `/persons` + `/statistics-events` +
`/statistics-categories` REST endpoints — is shared, byte-for-byte, with demo #1.

## A. Manual walkthrough

### A.0 Prerequisites

Build once and start the shared database (the demo reuses the MariaDB from `docker-compose`):

```
cd ddd-cqrs-4-java-example
./mvnw install
docker-compose up            # MariaDB on :3306 (KurrentDB is not needed for this demo)
```

### A.1 Start the query and command services on the `esc-jpa` profile

Start the **query** service first (it owns the schema), then the **command** service, both with the
`esc-jpa` profile active and both pointed at the shared database:

```
# console 2 — query, listens on :8080
cd ddd-cqrs-4-java-example/spring-boot/query
SPRING_PROFILES_ACTIVE=esc-jpa ./mvnw spring-boot:run

# console 3 — command, listens on :8081
cd ddd-cqrs-4-java-example/spring-boot/command
SPRING_PROFILES_ACTIVE=esc-jpa ./mvnw spring-boot:run
```

### A.2 Run the round trip

```
# console 4
cd ddd-cqrs-4-java-example/demo/backend-portability
./run-backend-portability-demo.sh
```

`run-backend-portability-demo.sh` performs the complete flow and waits for the projection at each
step (`COMMAND_URL`, `QUERY_URL`, `TIMEOUT_SECONDS` overrides are honoured):

1. **Preflight** — checks both services are reachable.
2. **Initial state** — the query read model is empty.
3. **Create** — POSTs the three `create-*-command.json` commands to the command service.
4. **Project** — polls the query service until all three persons appear — the command → relational
   event store → catch-up projection propagation happening live.
5. **Query** — reads them back over REST (`/persons`, `/persons/{id}`, `/statistics-events`, `/statistics-categories`).
6. **Delete** — sends `delete-harry-osborn-command.json`.
7. **Project + verify** — polls until "Harry Osborn" is gone (`GET /persons/{id}` → `404`).

### A.3 Look behind the scenes

- The **command** service log shows the aggregate persisting, e.g.
  `Update aggregate: streamId=PERSON-954177c4-...`.
- The **query** service log shows the polling projection consuming events, e.g.
  `Handle PersonCreatedEvent: Person 'Harry Osborn' (954177c4-...) was created`.
- Inspect the relational database directly: the `events` table holds the raw event log the command
  service appended, and the read-model tables hold the derived view the query service projected from
  it — the same source of truth both services agree on, now in SQL.

## B. Automated test

[`PersonJpaBackendE2EIT`](../../spring-boot/query/src/test/java/org/fuin/cqrs4j/example/spring/query/e2e/PersonJpaBackendE2EIT.java)
is the automated form of this demo — the same round trip as demo #1's `PersonE2EIT`, but with
`@ActiveProfiles("esc-jpa")` so the whole pipeline runs over the relational store. The
`docker-maven-plugin` starts MariaDB for the `integration-test` phase, so no manual setup is needed:

```
cd ddd-cqrs-4-java-example
./mvnw -f spring-boot/pom.xml -pl query -am verify
```

Like `PersonE2EIT` it drives the command side's **real** `Person` aggregate and event-sourced
`EventStorePersonRepository` in-process against the query application's own event-store connection,
then `await()`s until the query projection has caught up and asserts `GET /persons/{id}`,
`GET /persons`, `GET /statistics-events`, and `GET /statistics-categories` over REST — proving the read model still converges when the
backend is the relational store.

## What this proves

- **Backend portability** — the identical command/query/projection code runs unchanged over a
  completely different event-store backend; only a Spring profile is swapped.
- **SPI-only coupling** — the domain and projection depend on the `EventStore` SPI, never on a
  concrete store, which is what makes the swap a wiring change rather than a rewrite.
- **Rebuildable read model** — on the relational backend the projection is a derived view over the
  event log that catches up by polling and can be rebuilt from those events at any time.
