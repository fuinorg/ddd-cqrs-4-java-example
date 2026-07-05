# Demo: Rolling-Deploy Event Versioning

| Status | Effort | Depends on |
|--------|--------|-----------|
| 📋 planned — not yet implemented | M (2–4 days) | — |

> **Scripts:** none yet. This demo is **self-contained** — its own scripts and versioned JSON payloads
> land in this folder. See the [demos roadmap](../../demos-roadmap.md) for how this fits with the other
> demos.

## What it demonstrates

Evolving the event schema (e.g. `PersonCreatedEvent` v1 → v2 that splits `name` into
`firstName`/`lastName`) **without downtime and without rewriting history** — old v1 events coexist with
new v2 events in the same stream and are **upcast** to the current shape on read. This is the
event-sourcing answer to schema migration.

## Prerequisites

- An upcasting/serializer-versioning seam in `event-store-commons` (per-event `type` + version metadata,
  and a registry of upcasters). Confirm `esc`'s serialized-data metadata carries a version and that an
  upcaster hook exists; if only `type` is versioned, add a small upcaster chain in the example.
- The events already carry a stable logical type name (they do — `PersonCreatedEvent` etc.).

## Approach

1. Introduce `PersonCreatedEvent` **v2** (split name), keep v1 deserializable.
2. Register an upcaster v1→v2 (map `name` → `firstName`/`lastName`) applied on read.
3. Seed a stream with v1 events (this demo's own seed script), deploy the v2 code, append v2 events — show
   both project correctly through the single current handler.

## Verify

- Script: append a v1 event (pre-recorded JSON in this folder), then a v2 event, then query — both appear
  uniformly.
- IT: write raw v1 bytes to a stream, start the v2 projection, assert the read model has the upcast shape
  for both; assert a v1-only replay still rebuilds correctly.

## Open questions

Upcast on read vs. lazy in-place rewrite? Confirm `esc` metadata versioning granularity.
