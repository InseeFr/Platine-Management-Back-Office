--liquibase formatted sql

--changeset j75xzr:071_fix_scores_questioning_smallint_to_int.sql

ALTER TABLE questioning
    ALTER COLUMN score TYPE integer,
    ALTER COLUMN score_init TYPE integer;