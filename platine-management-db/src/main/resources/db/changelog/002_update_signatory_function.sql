--liquibase formatted sql

--changeset bettybecuwe:002_update_signatory_function.sql


ALTER TABLE public."owner" add signatory_function varchar NULL;
ALTER TABLE public."owner" DROP COLUMN signatory_fonction;

ALTER TABLE public."support" add signatory_function varchar NULL;
ALTER TABLE public."support" DROP COLUMN signatory_fonction;
