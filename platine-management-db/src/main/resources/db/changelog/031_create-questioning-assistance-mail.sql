--liquibase formatted sql

--changeset bettybecuwe:031_create-questioning-assistance-mail.sql

ALTER TABLE public.questioning add assistance_mail varchar NULL;