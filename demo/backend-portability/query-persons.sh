#!/usr/bin/env bash
# Query the read model (query service, port 8080).
# With no argument it lists all persons; with an id it fetches that single person.
#
#   ./query-persons.sh                                            # GET /persons
#   ./query-persons.sh 84565d62-115e-4502-b7c9-38ad69c64b05       # GET /persons/{id}
set -euo pipefail

QUERY_URL="${QUERY_URL:-http://localhost:8080}"

if [ $# -ge 1 ]; then
  curl -sS -H "Accept:application/json" "${QUERY_URL}/persons/$1"
else
  curl -sS -H "Accept:application/json" "${QUERY_URL}/persons"
fi
echo
