#!/bin/sh
set -eu
# Password arrives through the container environment and is quoted by psql.
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<'SQL'
\getenv app_password LUMEN_DB_APP_PASSWORD
SELECT format('CREATE ROLE lumen_app LOGIN NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS PASSWORD %L', :'app_password') \gexec
GRANT USAGE ON SCHEMA public TO lumen_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO lumen_app;
SQL
