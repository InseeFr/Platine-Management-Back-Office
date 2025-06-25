--liquibase formatted sql

--changeset corbincamille:040_create_source_type.sql

ALTER TABLE public.source ADD COLUMN type varchar NULL DEFAULT 'HOUSEHOLD';
