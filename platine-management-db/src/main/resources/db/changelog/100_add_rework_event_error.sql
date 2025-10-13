--liquibase formatted sql

--changeset j75xzr:100_add_rework_event_error.sql

INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(12, 2, 'NOQUAL');