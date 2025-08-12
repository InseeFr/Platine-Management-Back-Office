--liquibase formatted sql

--changeset prwozny:091_fix_remove_datacollection_target_check.sql

ALTER TABLE public.campaign
DROP CONSTRAINT campaign_datacollection_target_check;