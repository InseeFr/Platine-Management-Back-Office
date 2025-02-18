--liquibase formatted sql

--changeset davdarras:002-01

ALTER TABLE public.campaign ADD COLUMN IF NOT EXISTS datacollection_target varchar(255) NULL;
ALTER TABLE public.campaign ADD COLUMN IF NOT EXISTS sensitivity bool DEFAULT false NULL;
ALTER TABLE public.campaign ADD CONSTRAINT campaign_datacollection_target_check CHECK (datacollection_target::text = ANY (ARRAY['LUNATIC_SENSITIVE', 'LUNATIC_NORMAL', 'XFORM1', 'XFORM2']::text[]));

--changeset davdarras:002-02

UPDATE public.campaign set datacollection_target='LUNATIC_NORMAL';
UPDATE public.campaign set sensitivity=false;