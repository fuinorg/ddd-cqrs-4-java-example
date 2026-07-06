#!/usr/bin/env bash
# Query the statistics read model (query service, port 8080).
# With no argument it lists all statistic entries; with a type it fetches that single entry.
#
#   ./query-statistics.sh              # GET /statistics
#   ./query-statistics.sh person       # GET /statistics/{name}
set -euo pipefail

QUERY_URL="${QUERY_URL:-http://localhost:8080}"

if [ $# -ge 1 ]; then
  curl -sS -H "Accept:application/json" "${QUERY_URL}/statistics/$1"
else
  curl -sS -H "Accept:application/json" "${QUERY_URL}/statistics"
fi
echo
