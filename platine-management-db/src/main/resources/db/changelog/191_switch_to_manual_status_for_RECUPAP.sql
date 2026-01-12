--liquibase formatted sql

--changeset pgpu0i:191_switch_to_manual_status_for_RECUPAP.sql

UPDATE questioning_event
SET status = 'MANUAL'
WHERE type IN ('RECUPAP');