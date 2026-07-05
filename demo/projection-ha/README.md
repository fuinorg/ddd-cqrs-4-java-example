# Demo: Projection High-Availability

| Status | Effort | Depends on |
|--------|--------|-----------|
| 📋 planned — not yet implemented | L (1 week+) | cqrs-4-java lease work |

> **Scripts:** none yet. This demo is **self-contained** — it carries its own driver script (which starts
> two query instances and simulates a failover) plus its own command payloads in this folder. See the
> [demos roadmap](../../demos-roadmap.md) for how this fits with the other demos.

## What it demonstrates

Running **two query-service instances** against the same event store where only **one** actively advances
each projection (leader), the other on **hot standby**; if the leader dies, the standby takes over the
projection **without double-processing** events. This is the availability story for the read side.

## Prerequisites

- A distributed lease/lock in `cqrs-4-java`'s view managers. There is already a projection **position**
  (`readProjectionPosition`); add/finish a **lease** with owner + TTL so only the lease holder projects.
  This is the core library work (effort **L**).
- A shared store for the lease + position (the existing MariaDB read DB is fine).

## Approach

1. Add a lease acquire/renew around the catch-up loop in `QuarkusViewManager` / `SpringViewManager`
   (holder projects; non-holders idle and poll the lease).
2. Start two query instances (`:8080` and `:8082`) sharing the read DB.
3. Drive creates; show only one instance's log projects; kill it; show the other acquires the lease within
   the TTL and continues from the persisted position — no gaps, no duplicates.

## Verify

- Script: start two instances, drive the round trip, `kill` the leader, re-query the surviving instance.
- IT: two in-process view managers sharing a DB; assert exactly-once projection across a simulated
  leader failover (kill the holder, assert the position advances on the other with no reprocessing).

## Open questions

Lease TTL vs. projection batch time; fencing to prevent a paused-then-resumed old leader from writing
stale positions.
