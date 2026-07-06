#!/usr/bin/env bash
# Query the statistics read models (query service, port 8080).
#
# There are two independent statistic projections:
#   * event-type based  -> /statistics-events      (counts live instances per entity type, e.g. person)
#   * category based    -> /statistics-categories  (counts events per category, e.g. created / deleted)
#
#   ./query-statistics.sh                       # list both statistics
#   ./query-statistics.sh events [name]         # GET /statistics-events[/{name}]
#   ./query-statistics.sh categories [category] # GET /statistics-categories[/{category}]
set -euo pipefail

QUERY_URL="${QUERY_URL:-http://localhost:8080}"

get() {
  curl -sS -H "Accept:application/json" "$1"
  echo
}

kind="${1:-all}"
key="${2:-}"

case "${kind}" in
  events)
    get "${QUERY_URL}/statistics-events${key:+/$key}"
    ;;
  categories)
    get "${QUERY_URL}/statistics-categories${key:+/$key}"
    ;;
  all)
    echo "events:"
    get "${QUERY_URL}/statistics-events"
    echo "categories:"
    get "${QUERY_URL}/statistics-categories"
    ;;
  *)
    echo "Usage: $0 [events|categories] [key]" >&2
    exit 1
    ;;
esac
