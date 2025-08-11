--liquibase formatted sql

--changeset j75xzr:090_add_type_and_date_highest_status_event.sql

ALTER TABLE questioning
    ADD COLUMN highest_event_type varchar,
    ADD COLUMN highest_event_date timestamp;