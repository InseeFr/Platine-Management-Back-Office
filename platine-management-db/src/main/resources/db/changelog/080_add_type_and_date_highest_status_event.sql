--liquibase formatted sql

--changeset j75xzr:080_add_type_and_date_highest_status_event

ALTER TABLE questioning
    ADD COLUMN highest_type_event varchar,
    ADD COLUMN highest_date_event timestamp;