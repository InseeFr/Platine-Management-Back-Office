--liquibase formatted sql

--changeset j75xzr:040_add_operation_upload_reference_to_campaign.sql

ALTER TABLE public.campaign add operation_upload_reference varchar(255) NULL;