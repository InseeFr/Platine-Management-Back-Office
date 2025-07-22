--liquibase formatted sql

--changeset j75xzr:070_add_rework_variables_and_events.sql

ALTER TABLE questioning
    ADD COLUMN score INT,
    ADD COLUMN score_init INT;

INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(8, 2, 'EXPERT');
INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(9, 2, 'ONGEXPERT');
INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(10, 2, 'VALID');
INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(11, 2, 'ENDEXPERT');