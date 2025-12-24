--liquibase formatted sql

--changeset j75xzr:020-01
CREATE TABLE public.interrogation_event_order
(
    id     int8         NOT NULL,
    event_order  int4         NOT NULL,
    status varchar(255) NULL,
    CONSTRAINT interrogation_event_order_pkey PRIMARY KEY (id)
);

INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(1, 1, 'INITLA');
INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(2, 2, 'PARTIELINT');
INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(3, 2, 'VALINT');
INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(4, 2, 'RECUPAP');
INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(5, 3, 'REFUSAL');
INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(6, 3, 'WASTE');
INSERT INTO public.interrogation_event_order (id, event_order, status) VALUES(7, 4, 'HC');