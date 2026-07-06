#!/usr/bin/env bash
#
# Backend-portability demo (relational esc-jpa event store + catch-up projection).
#
# Drives the exact same CQRS / Event-Sourcing round trip as the end-to-end demo:
#
#     command REST (:8081)  ->  event store  ->  query projection  ->  query REST (:8080)
#
# ...but the services run against the relational esc-jpa backend instead of KurrentDB. Nothing in
# the domain, the projection or the REST API changes - only the event store behind the EventStore
# SPI - so this driver script is backend-agnostic: it just POSTs commands, waits for the query
# projection to catch up, reads the query REST API back, deletes one person and watches it
# disappear. The observable behaviour is identical; only the store underneath differs.
#
# What makes it the "esc-jpa" run is how the services are started (see README.md):
#     SPRING_PROFILES_ACTIVE=esc-jpa
# On this backend the query projection catches up by polling the relational store.
#
# Prerequisites (see README.md):
#   1. A shared relational database (the demo reuses the MariaDB from docker-compose).
#   2. A command service on :8081 started with SPRING_PROFILES_ACTIVE=esc-jpa.
#   3. A query   service on :8080 started with SPRING_PROFILES_ACTIVE=esc-jpa.
#
# Usage:
#   cd ddd-cqrs-4-java-example/demo/backend-portability
#   ./run-backend-portability-demo.sh
#
set -euo pipefail

cd "$(dirname "$0")"

COMMAND_URL="${COMMAND_URL:-http://localhost:8081}"
QUERY_URL="${QUERY_URL:-http://localhost:8080}"
export QUERY_URL

HARRY_ID="954177c4-aeb7-4d1e-b6d7-3e02fe9432cb"   # Harry Osborn (created, then deleted below)
PETER_ID="84565d62-115e-4502-b7c9-38ad69c64b05"   # Peter Parker
TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-20}"

step()  { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }
info()  { printf '    %s\n' "$*"; }
fail()  { printf '\n\033[1;31mERROR: %s\033[0m\n' "$*" >&2; exit 1; }

# Count the persons currently in the query read model (JSON array of {id,name}).
persons_count() {
  curl -sS -H "Accept:application/json" "${QUERY_URL}/persons" | grep -o '"id"' | wc -l | tr -d ' '
}

# HTTP status of GET /persons/{id} on the query service.
person_status() {
  curl -sS -o /dev/null -w '%{http_code}' -H "Accept:application/json" "${QUERY_URL}/persons/$1"
}

# Block until "$1" evaluates to the expected value "$2", or time out.
wait_until() {
  local describe="$1" probe="$2" expected="$3" waited=0
  info "waiting for: ${describe} (up to ${TIMEOUT_SECONDS}s) ..."
  while true; do
    if [ "$(eval "$probe")" = "$expected" ]; then
      info "ok"
      return 0
    fi
    [ "$waited" -ge "$TIMEOUT_SECONDS" ] && fail "timed out waiting for: ${describe}"
    sleep 1
    waited=$((waited + 1))
  done
}

# ---- 0. Preflight -----------------------------------------------------------------------------
step "Preflight: both services reachable (esc-jpa profile)?"
curl -sS -o /dev/null "${COMMAND_URL}/actuator/health" 2>/dev/null \
  || fail "command service not reachable at ${COMMAND_URL} (start it with SPRING_PROFILES_ACTIVE=esc-jpa, see README.md)"
info "command service up at ${COMMAND_URL}"
curl -sS -o /dev/null "${QUERY_URL}/persons" \
  || fail "query service not reachable at ${QUERY_URL} (start it with SPRING_PROFILES_ACTIVE=esc-jpa, see README.md)"
info "query service up at ${QUERY_URL}"

# ---- 1. Initial state -------------------------------------------------------------------------
step "Initial query read model (expected: empty)"
./query-persons.sh
./query-statistics.sh

# ---- 2. Command side: create three persons ----------------------------------------------------
step "Sending create commands to the command service (${COMMAND_URL}/persons/create)"
for file in ./create-*-command.json; do
  info "POST ${file}"
  curl -sS -o /dev/null -w '    -> HTTP %{http_code}\n' \
    -H "Content-Type:application/json" -d "@${file}" \
    "${COMMAND_URL}/persons/create"
done

# ---- 3. Query side: projection catches up from the relational event store ---------------------
step "Query side polling the PersonCreatedEvents from the relational event store"
wait_until "3 persons in the read model" persons_count 3

step "Query read model after create"
./query-persons.sh
info "single person (Peter Parker):"
./query-persons.sh "${PETER_ID}"
info "statistics:"
./query-statistics.sh

# ---- 4. Command side: delete one person -------------------------------------------------------
step "Deleting 'Harry Osborn' via the command service (${COMMAND_URL}/persons/${HARRY_ID})"
curl -sS -o /dev/null -w '    -> HTTP %{http_code}\n' \
  -X DELETE -H "Content-Type:application/json" \
  -d "@delete-harry-osborn-command.json" \
  "${COMMAND_URL}/persons/${HARRY_ID}"

# ---- 5. Query side: projection removes it -----------------------------------------------------
step "Query side polling the PersonDeletedEvent"
wait_until "'Harry Osborn' gone from the read model (GET /persons/${HARRY_ID} -> 404)" \
  "person_status ${HARRY_ID}" 404

step "Final query read model after delete (Harry Osborn no longer present)"
./query-persons.sh
info "statistics:"
./query-statistics.sh

step "Done - same round trip, relational backend: command -> esc-jpa event store -> catch-up projection -> query REST."
