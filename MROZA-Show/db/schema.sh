#!/bin/bash
set -e

PGPASSWORD=123456

psql -v ON_ERROR_STOP=1 -U mroza -d mrozadb -f scripts/create_db.sql
