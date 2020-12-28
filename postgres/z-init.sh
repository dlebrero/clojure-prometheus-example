#!/usr/bin/env bash

set -eu

psql -c "CREATE ROLE adbuser WITH PASSWORD 'adbuserpassword' CREATEDB LOGIN;"
psql -c "CREATE DATABASE a_sample_db WITH OWNER = adbuser TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';"

psql -d a_sample_db -U adbuser -c "CREATE TABLE users (id SERIAL PRIMARY KEY, email varchar(200) UNIQUE NOT NULL);"