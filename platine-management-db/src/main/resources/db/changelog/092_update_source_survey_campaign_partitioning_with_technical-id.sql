--liquibase formatted sql

--changeset ethuaud:092_update_source_survey_campaign_partitioning_with_technical_id.sql

ALTER TABLE public."campaign"
ADD COLUMN "technical_id" UUID;

ALTER TABLE public."partitioning"
ADD COLUMN "technical_id" UUID;
