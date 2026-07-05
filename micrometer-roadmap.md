# Micrometer Instrumentation Roadmap

Cross-repo roadmap for adding [Micrometer](https://micrometer.io/) metrics across the three
libraries this example builds on: **event-store-commons**, **ddd-4-java**, and **cqrs-4-java**.

## Why

Today only **cqrs-4-java** uses Micrometer, and only as `Gauge`/`MeterBinder` **state** meters
(outbox depth, dead-letter count, process-timeout pending/overdue, per-view projection lag).
**event-store-commons** and **ddd-4-java** have **zero** Micrometer usage — they are
framework-neutral libraries with only SLF4J logging and no `MeterRegistry` seam.

The gap is **throughput / latency / outcome** meters (`Counter` / `Timer` / `DistributionSummary`)
on the hot paths — append/read latency, optimistic-concurrency conflict rates, crypto
key-service latency, catch-up/projection throughput, command-dispatch and outbox-delivery
outcomes. These hot paths live mostly in the two neutral upstream libraries.

Goal: **general observability across all three repos**, while keeping Micrometer strictly
optional and zero-impact when no registry is present.

## Task lists (per repo)

- **event-store-commons** → [micrometer-tasks.md](https://github.com/fuinorg/event-store-commons/blob/develop/micrometer-tasks.md)
- **ddd-4-java** → [micrometer-tasks.md](https://github.com/fuinorg/ddd-4-java/blob/develop/micrometer-tasks.md)
- **cqrs-4-java** → [micrometer-tasks.md](https://github.com/fuinorg/cqrs-4-java/blob/develop/micrometer-tasks.md)

## Architecture — decorator-first hybrid

No `io.micrometer.*` import in any neutral hot-path class. Two mechanisms:

1. **Interface decorators** (backbone) for latency/throughput/outcome. Each decorator implements
   the existing interface, wraps a delegate, takes a `MeterRegistry` in its constructor, and is
   instantiated at the **cqrs framework layer** (where a registry already exists). This preserves
   the current "absent registry ⇒ never wrapped ⇒ app unaffected" property.
   [`EncryptingEventStore`](https://github.com/fuinorg/event-store-commons/blob/develop/crypto/src/main/java/org/fuin/esc/crypto/EncryptingEventStore.java)
   already demonstrates this shape (`implements EventStore`, holds `EventStore delegate`, has a `Builder`).
2. **A micrometer-free facade** (`EscMetrics` / `Ddd4jMetrics`, with a `NOOP` default) for the four
   intra-method seams a decorator cannot observe: the JPA pessimistic-lock wait, the crypto
   key-service round-trip, the catch-up pass loop, and the pg NOTIFY-vs-poll branch. Neutral
   modules depend only on the facade interface; the Micrometer implementation lives in the opt-in
   module.

All Micrometer code for each upstream repo lives in **one new optional module**
(`event-store-commons/micrometer`, `ddd-4-java/micrometer`). An **ArchUnit rule** (both repos
already use ArchUnit) forbids `io.micrometer` imports outside those modules.

### Double-counting rule — instrument at exactly ONE layer

The `EventStore` contract is re-exposed by `DelegatingAsyncEventStore` (async→sync),
`spi/DelegatingSyncEventStore` (sync→async), and the `EncryptingEventStore` decorator. Rule:
**wrap the concrete backend only** —
[`MeteredEventStoreAsync`] around
[`ESGrpcEventStoreAsync`](https://github.com/fuinorg/event-store-commons/blob/develop/esgrpc/src/main/java/org/fuin/esc/esgrpc/ESGrpcEventStoreAsync.java)
/ [`InMemoryEventStoreAsync`](https://github.com/fuinorg/event-store-commons/blob/develop/mem/src/main/java/org/fuin/esc/mem/InMemoryEventStoreAsync.java),
[`MeteredEventStore`] around
[`JpaEventStore`](https://github.com/fuinorg/event-store-commons/blob/develop/jpa/src/main/java/org/fuin/esc/jpa/JpaEventStore.java)
— **never** around a `Delegating*` bridge or `EncryptingEventStore`. Tag `backend=mem|pg|jpa|esgrpc`.
`EncryptingEventStore` gets its own disjoint `esc.crypto.*` meters. A composition test enforces
"exactly once per logical op".

## Naming & tags

Extends the existing `cqrs4j.*` convention with `esc.*` and `ddd4j.*`. Meter names as
`public static final String` constants, dotted `namespace.<area>.<metric>`.
**Bounded-cardinality tags only**: `operation`, `outcome` (success|failure|conflict|deduplicated|notfound),
`backend`, `contentType`, `event.type`, `cmd.type`, `aggregate.type`, `result` (hit|miss).
**Never** tag by streamId / aggregateId / tenantId.

| Namespace | Example meters |
|-----------|----------------|
| `esc.*`   | `esc.eventstore.append\|read\|delete` Timer; `esc.eventstore.errors` Counter; `esc.crypto.encrypt\|decrypt` Timer + `esc.crypto.undecryptable` Counter; `esc.jpa.lock` Timer; `esc.catchup.pass` Timer + `esc.catchup.events\|errors` Counter + `esc.catchup.lag` Gauge; `esc.pg.wakeup` Counter (source=notify\|poll); `esc.serde.serialize\|deserialize` Timer + `esc.serde.payload.bytes` DistributionSummary |
| `ddd4j.*` | `ddd4j.aggregate.read\|update` Timer; `ddd4j.aggregate.cache` Counter (result=hit\|miss); `ddd4j.aggregate.conflict` Counter; `ddd4j.aggregate.retries` DistributionSummary; `ddd4j.aggregate.add.exists` Counter; `ddd4j.aggregate.events.applied` Counter; `ddd4j.handler.lookup` Timer + `ddd4j.handler.missing` Counter; `ddd4j.serde.*` |
| `cqrs4j.*` | `cqrs4j.command.dispatch` Timer (cmd.type, outcome); `cqrs4j.projection.apply` Timer+Counter; `cqrs4j.command.delivery` Timer + `cqrs4j.command.batch.size` DistributionSummary + `cqrs4j.command.failure\|deadletter` Counter; `cqrs4j.view.chunk` Timer + `cqrs4j.view.events` Counter; `cqrs4j.process.timeout.swept` Counter |

## The 20% set (highest value — do first)

1. `MeteredEventStoreAsync` wrapping `ESGrpcEventStoreAsync` → `esc.eventstore.append|read|delete` + `esc.eventstore.errors` (real network, highest signal).
2. `MeteredRepository` over ddd's `IEventStoreRepositoryAsync` → `ddd4j.aggregate.read|update` timers, `ddd4j.aggregate.conflict`, `ddd4j.aggregate.cache` hit/miss.
3. `cqrs4j.command.dispatch` Timer + outcome on both dispatchers (the "already handled ⇒ skip" branch already exists → `outcome=deduplicated`).
4. `esc.crypto.encrypt|decrypt` Timer + `esc.crypto.undecryptable` Counter (external key-service latency).

## Release ordering

Fixed by dependencies: **event-store-commons → ddd-4-java → cqrs-4-java** (cqrs imports `esc-bom`
+ `ddd-4-java-*`; ddd imports `esc-bom`). Each upstream release is backward-compatible because
facade seams default to `NOOP` and the decorator modules are additive/opt-in — they can ship
before the downstream consumes them. All three currently sit on aligned `1.0.2-SNAPSHOT` fuin-bom.

## Dependency declaration (keep optional)

- **event-store-commons** / **ddd-4-java**: import `io.micrometer:micrometer-bom` in the parent
  POM `<dependencyManagement>`. Neutral modules get **no** micrometer dependency (they see only the
  facade interface). The new `micrometer` module depends on `micrometer-core` (normal scope — the
  whole module is opt-in) and is registered in the repo BOM + parent `<modules>`.
- **cqrs-4-java**: unchanged mechanism — Spring modules keep `micrometer-core`
  `<optional>true</optional>` (Spring Boot BOM manages the version); Quarkus modules via the
  Quarkus BOM.

## Verification

- **Unit**: mirror the existing
  [`OutboxMetricsTest`](https://github.com/fuinorg/cqrs-4-java/blob/develop/quarkus/process-manager/src/main/java/org/fuin/cqrs4j/quarkus/pm/OutboxMetrics.java) /
  [`ProjectionLagMetrics`](https://github.com/fuinorg/cqrs-4-java/blob/develop/springboot/query-core/src/main/java/org/fuin/cqrs4j/springboot/query/core/view/ProjectionLagMetrics.java)
  tests — a `SimpleMeterRegistry` per meter, drive the wrapped delegate (use the real
  `InMemoryEventStore` as the no-network backend), assert counts / tags / timer counts.
- **Double-counting test**: compose a metered backend behind `DelegatingSyncEventStore` +
  `EncryptingEventStore`; assert the store timer fires exactly once per logical op.
- **Zero-impact-if-absent**: construct neutral classes with the `NOOP` facade; verify identical
  behavior and no `MeterRegistry` reference. ArchUnit rule forbids `io.micrometer` outside the
  `*-micrometer` modules.
- **Build**: `./mvnw -q verify` per repo in dependency order (esc → ddd → cqrs). Mind the recorded
  JDK-25 Byte Buddy flag and online central-snapshots build quirks.
- **End-to-end (this example app)**: with a registry present, append + read an aggregate through
  the metered gRPC/JPA backend and confirm `esc.eventstore.*`, `ddd4j.aggregate.*`, and
  `cqrs4j.command.dispatch` meters appear with expected tags and non-zero counts; confirm the same
  app with no registry starts and runs unchanged.
