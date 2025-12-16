--liquibase formatted sql

--changeset y72wvh:170_delete_useless_tables_and_columns

DROP TABLE public.campaign_event;

ALTER TABLE public."source" DROP COLUMN force_close;
ALTER TABLE public."source" DROP COLUMN message_info_survey_offline;
ALTER TABLE public."source" DROP COLUMN message_survey_offline;
