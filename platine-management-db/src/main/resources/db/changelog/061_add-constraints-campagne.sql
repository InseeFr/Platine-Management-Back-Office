--liquibase formatted sql

--changeset farid aitkarra:061

ALTER TABLE public.campaign ALTER COLUMN sensitivity SET NOT NULL;
ALTER TABLE public.campaign ALTER COLUMN datacollection_target SET NOT NULL;
