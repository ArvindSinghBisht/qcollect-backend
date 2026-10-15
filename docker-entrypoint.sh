#!/bin/sh
# Maps Render's Postgres URL onto the datasource properties this app already reads.
# The process command is the repo's packaged jar, bound to Render's PORT.
set -eu

port="${PORT:-8080}"

if [ -z "${SPRING_DATASOURCE_URL:-}" ] && [ -n "${DATABASE_URL:-}" ]; then
  hostportdb="${DATABASE_URL#*@}"
  hostportdb="${hostportdb%%\?*}"
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${hostportdb}?sslmode=require"
fi

if [ -z "${SPRING_DATASOURCE_USERNAME:-}" ] && [ -n "${DB_USER:-}" ]; then
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
fi

if [ -z "${SPRING_DATASOURCE_PASSWORD:-}" ] && [ -n "${DB_PASSWORD:-}" ]; then
  export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"
fi

exec java -Dserver.port="${port}" -jar /app/app.jar
