--liquibase formatted sql

--changeset ethuaud:092_update_source_survey_campaign_partitioning_with_technical_id.sql

-- === CAMPAIGN ===

-- Step 1: Add the technical_id column (UUID type, nullable at first)
ALTER TABLE public."campaign"
ADD COLUMN "technical_id" UUID;

-- Step 2: Update existing rows with random UUIDs
UPDATE public."campaign"
SET "technical_id" = gen_random_uuid()
WHERE "technical_id" IS NULL;

-- Step 3: Add NOT NULL constraint
ALTER TABLE public."campaign"
ALTER COLUMN "technical_id" SET NOT NULL;

-- Step 4: Add a unique constraint on the technical_id column
ALTER TABLE public."campaign"
ADD CONSTRAINT campaign_unique_technical_id UNIQUE ("technical_id");

-- === PARTITIONING ===

-- Step 1: Add the technical_id column (UUID type, nullable at first)
ALTER TABLE public."partitioning"
ADD COLUMN "technical_id" UUID;

-- Step 2: Update existing rows with random UUIDs
UPDATE public."partitioning"
SET "technical_id" = gen_random_uuid()
WHERE "technical_id" IS NULL;

-- Step 3: Add NOT NULL constraint
ALTER TABLE public."partitioning"
ALTER COLUMN "technical_id" SET NOT NULL;

-- Step 4: Add a unique constraint on the technical_id column
ALTER TABLE public."partitioning"
ADD CONSTRAINT partitioning_unique_technical_id UNIQUE ("technical_id");