--liquibase formatted sql

--changeset j75xzr:150_add_priority_interrogation
ALTER TABLE questioning ADD COLUMN priority BIGINT;

CREATE INDEX idx_questioning_priority ON questioning(priority);