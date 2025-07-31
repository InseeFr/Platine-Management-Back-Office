--liquibase formatted sql

--changeset j75xzr:090_add_type_and_date_highest_status_event.sql

ALTER TABLE questioning
    ADD COLUMN highest_type_event varchar,
    ADD COLUMN highest_date_event timestamp;