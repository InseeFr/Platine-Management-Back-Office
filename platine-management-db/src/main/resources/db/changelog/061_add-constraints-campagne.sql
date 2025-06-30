--liquibase formatted sql

--changeset farid aitkarra:061

UPDATE public.campaign SET sensitivity = false WHERE sensitivity IS NULL;
UPDATE public.campaign SET datacollection_target = 'LUNATIC_NORMAL' WHERE datacollection_target IS NULL;

ALTER TABLE public.campaign ALTER COLUMN sensitivity SET NOT NULL;
ALTER TABLE public.campaign ALTER COLUMN datacollection_target SET NOT NULL;
