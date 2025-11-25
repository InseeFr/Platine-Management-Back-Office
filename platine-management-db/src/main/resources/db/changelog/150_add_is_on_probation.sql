--liquibase formatted sql

--changeset pgpu0i:150_add_is_on_probation.sql

ALTER TABLE questioning
ADD COLUMN is_on_probation BOOLEAN;

UPDATE questioning
SET is_on_probation = false;

ALTER TABLE questioning
ALTER COLUMN is_on_probation SET DEFAULT false;

ALTER TABLE questioning
ALTER COLUMN is_on_probation SET NOT NULL;
