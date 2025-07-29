--liquibase formatted sql

--changeset bettybecuwe:080_add-period-collect-value.sql

ALTER TABLE public.campaign ADD period_collect_value varchar NULL;

UPDATE public.campaign SET period_collect_value = period_value WHERE period_collect_value IS NULL;

