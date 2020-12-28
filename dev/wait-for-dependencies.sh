#!/usr/bin/env bash

set -eu

MAX_ATTEMPTS=120

echo "Waiting for PostgreSQL ..."

ATTEMPTS=0
PG=""
SQL="SELECT 1"

while [[ -z "${PG}" && "${ATTEMPTS}" -lt "${MAX_ATTEMPTS}" ]]; do
    export PGPASSWORD=adbuserpassword
    PG=$( (psql --username=adbuser --host=postgres --dbname=a_sample_db --command "${SQL}" 2>&1 | grep "(1 row)") || echo "")
    let ATTEMPTS+=1
    sleep 1
done

if [[ -z "${PG}" ]]; then
    echo "PostgreSQL is not available"
    exit 1
fi

echo "PostgreSQL is ready!"
