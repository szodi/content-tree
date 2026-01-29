CREATE TYPE "jsonb" AS json;
CREATE TYPE "bigint[]" AS BIGINT ARRAY;
CREATE TYPE "TIME(0) WITHOUT TIME ZONE" AS TIME;
CREATE TYPE "TIMESTAMP(0) WITHOUT TIME ZONE" AS TIMESTAMP;
CREATE TYPE "TIMESTAMP(0) WITH TIME ZONE" AS TIMESTAMP;
CREATE TYPE "text" AS CHARACTER LARGE OBJECT;
CREATE TYPE "text[]" AS CHARACTER LARGE OBJECT ARRAY;

create table shedlock(name varchar(64) not null, lock_until timestamp not null, locked_at timestamp not null, locked_by varchar(255) not null);
