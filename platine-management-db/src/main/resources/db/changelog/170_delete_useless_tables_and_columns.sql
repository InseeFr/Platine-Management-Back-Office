--liquibase formatted sql

--changeset y72wvh:170_delete_useless_tables_and_columns

DROP TABLE IF EXISTS public.campaign_event;

ALTER TABLE public."source"
    DROP COLUMN IF EXISTS force_close,
    DROP COLUMN IF EXISTS message_info_survey_offline,
    DROP COLUMN IF EXISTS message_survey_offline;

ALTER TABLE public.internal_users
    DROP COLUMN IF EXISTS creation_author,
    DROP COLUMN IF EXISTS creation_date,
    DROP COLUMN IF EXISTS first_name,
    DROP COLUMN IF EXISTS "name",
    DROP COLUMN IF EXISTS organization;
