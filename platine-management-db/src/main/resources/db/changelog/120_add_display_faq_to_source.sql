--liquibase formatted sql

--changeset pgpu0i:120_add_display_faq_to_source.sql

ALTER TABLE source
    ADD COLUMN IF NOT EXISTS display_faq BOOLEAN DEFAULT FALSE;


