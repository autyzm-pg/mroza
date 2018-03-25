FROM postgres
COPY scripts/init.sql /docker-entrypoint-initdb.d/
COPY schema.sh /docker-entrypoint-initdb.d/
