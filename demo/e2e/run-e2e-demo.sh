#!/usr/bin/env bash
#
# End-to-end CQRS / Event-Sourcing demo.
#
# Drives the full round trip that the two microservices integrate through:
#
#     command REST (:8081)  ->  event store  ->  query projection  ->  query REST (:8080)
#
# The command and query services never call each other directly. This script POSTs create
# commands to the command service, waits for the query service's projection to catch up from
# the event store, reads the query REST API back, then deletes one person and watches it
# disappear from the read model. Works the same whether the running services are the Spring
# Boot or the Quarkus implementation (you can even mix the two stacks).
#
# Prerequisites (see README.md):
#   1. docker-compose up                     # KurrentDB (:2113) + MariaDB (:3306)
#   2. a command service listening on :8081  # Spring Boot or Quarkus
#   3. a query service   listening on :8080  # Spring Boot or Quarkus
#
# Usage:
#   cd ddd-cqrs-4-java-example/demo/e2e
#   ./run-e2e-demo.sh
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
step "Preflight: both services reachable?"
curl -sS -o /dev/null "${COMMAND_URL}/actuator/health" 2>/dev/null \
  || curl -sS -o /dev/null "${COMMAND_URL}/q/health" 2>/dev/null \
  || fail "command service not reachable at ${COMMAND_URL} (start it, see README.md)"
info "command service up at ${COMMAND_URL}"
curl -sS -o /dev/null "${QUERY_URL}/persons" \
  || fail "query service not reachable at ${QUERY_URL} (start it, see README.md)"
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

# ---- 3. Query side: projection catches up from the event store --------------------------------
step "Query side projecting the PersonCreatedEvents from the event store"
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
step "Query side projecting the PersonDeletedEvent"
wait_until "'Harry Osborn' gone from the read model (GET /persons/${HARRY_ID} -> 404)" \
  "person_status ${HARRY_ID}" 404

step "Final query read model after delete (Harry Osborn no longer present)"
./query-persons.sh
info "statistics:"
./query-statistics.sh

step "Done — command -> event store -> query projection -> query REST verified end to end."
