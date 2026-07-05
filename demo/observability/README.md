# Demo: Observability

| Status | Effort | Depends on |
|--------|--------|-----------|
| 📋 planned — not yet implemented | S–M | cqrs-4-java metrics hook |

> **Scripts:** none yet. This demo is **self-contained** — it carries its own load-driver script, a
> metrics-scraping script, and its own Prometheus/Grafana `docker-compose` overlay in this folder. See
> the [demos roadmap](../../demos-roadmap.md) for how this fits with the other demos.

## What it demonstrates

Operating the system: **metrics** (command rate, projection lag, event-store call latency/errors),
**health** (event-store & DB reachability, projection liveness), and **tracing** a request across the
command → event-store → projection → query boundary.

## Prerequisites

- Micrometer (Spring Boot) / MicroProfile Metrics + OpenTelemetry (Quarkus) — both frameworks ship these;
  mostly configuration.
- Instrumentation seams in the libraries: a projection-lag gauge (current vs. head position — the position
  already exists), and event-store call timers. Some are config-only; a lag gauge needs a small hook in
  the view managers. The same event-store call sites that gain fault-tolerance (timeouts, retries) are the
  natural place to record latency/error metrics.

## Approach

1. Enable the metrics/health endpoints (`/actuator/prometheus`, `/q/metrics`, `/q/health`).
2. Add a **projection-lag** metric (head position − projected position) and event-store call timers.
3. Add distributed tracing so a create command and its downstream projection share a trace (propagate a
   trace/correlation id through event metadata).
4. Ship a small Prometheus + Grafana (or console) `docker-compose` overlay and a sample dashboard.

## Verify

- Script: drive the round trip under load, then `curl` the metrics endpoint and show projection-lag
  rising and draining.
- IT: assert the health endpoint reports the event store/DB, and that the projection-lag gauge is
  registered and returns to ~0 after the read model converges.

## Open questions

Trace propagation through the event store (custom event metadata vs. a header convention). Scope: start
with metrics + health (**S**), add tracing later (**M**).
