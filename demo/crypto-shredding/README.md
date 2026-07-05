# Demo: Crypto-Shredding (DDD-4)

| Status | Effort | Depends on |
|--------|--------|-----------|
| 📋 planned — not yet implemented | L (1 week+) | objects4j / ddd-4-java feature |

> **Scripts:** none yet. Scripts (`create → query → forget → query`) land in this folder once the
> library feature exists. See the [demos roadmap](../../demos-roadmap.md) for how this fits with the
> other demos.

## What it demonstrates

GDPR-style "right to be forgotten" on an **immutable** event log: personal data in events is stored
**encrypted** with a per-subject key; deleting (shredding) that key renders every event for the subject
unreadable, effectively erasing the PII without rewriting or deleting history. The `Person` aggregate is
the natural subject (its `name` is the PII).

## Prerequisites (largest unknown — likely a library feature first)

- A crypto-shredding facility in `ddd-4-java` / `objects4j`: an encrypting event serializer, a key store
  keyed by aggregate/subject id, and a "forget subject" operation that drops the key. **Check whether this
  exists yet**; if not, this demo is gated on adding it to the library (hence effort **L**).
- A key store backend for the demo (in-memory or a table in MariaDB).

## Approach

1. Mark the PII field(s) on `PersonCreatedEvent` for encryption (annotation or serializer config).
2. On create, generate/lookup a per-`PersonId` key; encrypt the PII on write, decrypt on projection.
3. Add a `POST /persons/{id}/forget` command that deletes the key.
4. Show that after "forget": existing streams still contain the (now-undecryptable) events, the aggregate
   can no longer materialize the name, and the read model can be scrubbed on the next projection.

## Verify

- Script: `create → query (name visible) → forget → query (name gone / redacted)`.
- IT: assert the ciphertext in the event store, decryptability before shredding, and undecryptability
  after; assert the read model reflects the erasure.

## Open questions

Where does key deletion leave in-flight projections? Do we redact or delete the read model row on forget?
Confirm the exact `ddd-4-java`/`objects4j` API (or scope the library task).
