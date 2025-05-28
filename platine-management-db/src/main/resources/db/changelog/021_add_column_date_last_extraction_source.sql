--liquibase formatted sql

-- changeset PhilippeTR:011-01
-- comment: Update source table to add new columns
ALTER TABLE source ADD COLUMN IF NOT EXISTS date_last_extraction timestamp DEFAULT NULL;
