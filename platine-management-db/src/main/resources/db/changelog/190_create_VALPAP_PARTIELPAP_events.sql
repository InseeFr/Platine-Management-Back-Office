--liquibase formatted sql

--changeset y72wvh:190_create_VALPAP_PARTIELPAP_events.sql

INSERT INTO public.interrogation_event_order
(id, event_order, status)
VALUES(13, 2, 'PARTIELPAP');
INSERT INTO public.interrogation_event_order
(id, event_order, status)
VALUES(14, 2, 'VALPAP');