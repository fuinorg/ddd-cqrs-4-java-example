# End-to-End Demo: Command → Event Store → Query

This walkthrough drives the **full CQRS / Event-Sourcing round trip** end to end and shows how the two
microservices integrate. It is the first of the demos in the [demos roadmap](../../demos-roadmap.md); the
other five are described there.

```
   command REST (:8081)  ─POST/DELETE─▶  ┌───────────────┐  ─subscribe─▶  query projection  ─JPA─▶  query REST (:8080)
                                         │  event store  │
   Person aggregate ──appends events──▶  │  (KurrentDB)  │  ──PersonCreatedEvent / PersonDeletedEvent──▶  read model
                                         └───────────────┘
```

The command service and the query service **never call each other directly**. The command side appends
events to the event store; the query side subscribes to those events and projects them into its JPA read
model, which it serves over REST. The event store is the *only* integration point — which is exactly what
makes it safe to run **either stack on either side, and even mix them** (Spring Boot command + Quarkus
query, or vice-versa).

## Files in this folder

| File | Purpose |
|------|---------|
| `run-e2e-demo.sh` | Orchestrator — runs the whole round trip and waits for the projection at each step. |
| `create-persons.sh` | POSTs the three `create-*-command.json` commands to the command service. |
| `delete-harry-osborn.sh` | DELETEs "Harry Osborn" via the command service. |
| `query-persons.sh` | Reads the query read model: `GET /persons` (or `/persons/{id}`). |
| `query-statistics.sh` | Reads the statistics read model: `GET /statistics` (or `/statistics/{name}`). |
| `create-*-command.json`, `delete-*-command.json` | The command payloads. |

There are two ways to see this:

- **[A. Manual walkthrough](#a-manual-walkthrough)** — start the services and drive them with the scripts
  in this folder. Good for exploring, for watching the projection catch up in the logs and in the
  KurrentDB UI, and for mixing the Quarkus and Spring Boot stacks.
- **[B. Automated test](#b-automated-test)** — a single self-contained integration test that spins up the
  infrastructure with Testcontainers and asserts the whole round trip. Good for CI.

---

## A. Manual walkthrough

### A.0 Prerequisites

Build once and start the infrastructure (KurrentDB on `:2113`, MariaDB on `:3306`) — see the main
[README](../../README.md) for details:

```
cd ddd-cqrs-4-java-example
./mvnw install            # console window 1: one-time build
docker-compose up         # console window 1 (or a new one): KurrentDB + MariaDB
```

### A.1 Start one query service and one command service

Start **one** query service and **one** command service. You can pick the same stack for both, or mix
them — the event store is the only contract between them.

**Spring Boot** (see [spring-boot.md](../../spring-boot.md)):

```
# console window 2 — query, listens on :8080
cd ddd-cqrs-4-java-example/spring-boot/query && ./mvnw spring-boot:run

# console window 3 — command, listens on :8081
cd ddd-cqrs-4-java-example/spring-boot/command && ./mvnw spring-boot:run
```

**Quarkus** (see [quarkus.md](../../quarkus.md)) — the query service also listens on `:8080` and the
command service on `:8081`, so the demo scripts and URLs below are identical:

```
# console window 2 — query, listens on :8080
cd ddd-cqrs-4-java-example/quarkus/query && ./mvnw quarkus:dev

# console window 3 — command, listens on :8081
cd ddd-cqrs-4-java-example/quarkus/command && ./mvnw quarkus:dev
```

> **Mixing stacks:** because both services only touch the event store, a Spring Boot command service and a
> Quarkus query service (or the reverse) interoperate with no change. Start the query from one stack and
> the command from the other and the walkthrough below behaves exactly the same — proof that the read and
> write sides are genuinely decoupled.

### A.2 Run the whole round trip with one script

```
# console window 4
cd ddd-cqrs-4-java-example/demo/e2e
./run-e2e-demo.sh
```

`run-e2e-demo.sh` performs the complete flow and waits for the projection at each step:

1. **Preflight** — checks both services are reachable.
2. **Initial state** — the query read model is empty (`GET /persons` → `[]`).
3. **Create** — POSTs the three `create-*-command.json` commands to the command service
   (`POST :8081/persons/create`).
4. **Project** — polls the query service until all three persons appear in the read model — this is the
   command → event store → query projection propagation happening live.
5. **Query** — reads them back: `GET :8080/persons`, `GET :8080/persons/{id}`, `GET :8080/statistics`.
6. **Delete** — sends `delete-harry-osborn-command.json` (`DELETE :8081/persons/{id}`).
7. **Project + verify** — polls the query service until "Harry Osborn" is gone
   (`GET :8080/persons/{id}` → `404`) and prints the final read model and statistics.

Environment overrides are honoured (`COMMAND_URL`, `QUERY_URL`, `TIMEOUT_SECONDS`) if you run the services
on different hosts/ports.

### A.3 Do it step by step (optional)

The orchestrator is just a wrapper around the individual scripts in this folder; you can run them by hand
to see each half in isolation:

```
cd ddd-cqrs-4-java-example/demo/e2e

./query-persons.sh                 # GET /persons        -> [] (empty)
./query-statistics.sh              # GET /statistics     -> [] (empty)

./create-persons.sh                # POST the 3 create commands to :8081

# ...watch the query service log project "PersonCreatedEvent" for each person...

./query-persons.sh                 # GET /persons        -> Harry, Mary Jane, Peter
./query-persons.sh 84565d62-115e-4502-b7c9-38ad69c64b05   # GET /persons/{id} -> Peter Parker
./query-statistics.sh              # GET /statistics     -> [ { "type": "person", "count": 3 } ]

./delete-harry-osborn.sh           # DELETE :8081/persons/954177c4-...

# ...watch the query service log project "PersonDeletedEvent"...

./query-persons.sh                 # GET /persons        -> Mary Jane, Peter (Harry gone)
./query-statistics.sh              # GET /statistics     -> [ { "type": "person", "count": 2 } ]
```

### A.4 Look behind the scenes

- **Query service log** shows the projection consuming events, e.g.
  `Handle PersonCreatedEvent: Person 'Harry Osborn' (954177c4-...) was created`.
- **Command service log** shows the aggregate persisting, e.g.
  `Update aggregate: streamId=PERSON-954177c4-..., version=-1, nextVersion=0`.
- **KurrentDB UI** at [http://localhost:2113](http://localhost:2113) → *Stream Browser* shows the
  per-aggregate stream `PERSON-84565d62-115e-4502-b7c9-38ad69c64b05` containing the raw
  `PersonCreatedEvent` — this is the source of truth both services agree on.

---

## B. Automated test

[`PersonE2EIT`](../../spring-boot/query/src/test/java/org/fuin/cqrs4j/example/spring/query/e2e/PersonE2EIT.java)
(in the Spring Boot query module) is a self-contained end-to-end integration test of the same round trip.
The `docker-maven-plugin` starts KurrentDB and MariaDB for the `integration-test` phase, so no manual
setup is needed:

```
cd ddd-cqrs-4-java-example
./mvnw -f spring-boot/pom.xml -pl query -am verify
```

Rather than run a second HTTP service, the test exercises the command side's **real** `Person` aggregate
and event-sourced `EventStorePersonRepository` in-process (a `test`-scoped dependency on the command
module) against the query application's own event-store connection. That is faithful to production
precisely *because* the two services share nothing but the event store:

1. **Command side** — `repo.add(new Person(...))` appends a `PersonCreatedEvent` to the
   `PERSON-<id>` stream in the event store.
2. **Query side** — `await()` until the query application's projection has consumed the event into the JPA
   read model; then assert `GET /persons/{id}`, `GET /persons`, and `GET /statistics` over REST
   (via RestAssured/MockMvc against the running Spring context).
3. **Command side** — `person.delete(); repo.update(person)` appends a `PersonDeletedEvent`.
4. **Query side** — `await()` until the read model drops it; assert `GET /persons/{id}` → `404`.

> **Packaging note:** the query module test-depends on the command module, so the command's
> `spring-boot-maven-plugin` is configured with `<classifier>exec</classifier>`. That keeps the plain,
> dependency-usable jar as the main artifact (the executable fat jar is attached under the `exec`
> classifier) so the command's domain classes are visible on the query test classpath. Running the app
> is unaffected — use `./mvnw spring-boot:run` or the `-exec` jar.

---

## What this proves

- **Command/Query separation** — writes go through the aggregate on the command side; reads are served
  from a projection on the query side. Neither service knows the other's address.
- **Event Sourcing** — the aggregate's state lives as a stream of events in the event store; the read
  model is a *derived* view that can be rebuilt from those events at any time.
- **Stack portability** — command and query can each be Spring Boot or Quarkus, because the event store is
  the only contract between them.

For the remaining demos (backend portability, crypto-shredding, rolling-deploy versioning, projection-HA,
observability) see the [demos roadmap](../../demos-roadmap.md).
