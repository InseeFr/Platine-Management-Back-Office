--liquibase formatted sql

--changeset ethuaud:_update_source_survey_campaign_partitioning_with_technical-id.sql

-- Step 1: Add the technical-id column (UUID type, not null)
ALTER TABLE public."campaign"
ADD COLUMN "technical-id" UUID NOT NULL;

-- Step 2: Update existing rows with random UUIDs
UPDATE public."campaign"
SET "technical-id" = gen_random_uuid();

-- Step 3: Add a unique constraint on the technical-id column
ALTER TABLE public."campaign"
ADD CONSTRAINT unique_technical_id UNIQUE ("technical-id");

-- Step 1: Add the technical-id column (UUID type, not null)
ALTER TABLE public."partitioning"
ADD COLUMN "technical-id" UUID NOT NULL;

-- Step 2: Update existing rows with random UUIDs
UPDATE public."partitioning"
SET "technical-id" = gen_random_uuid();

-- Step 3: Add a unique constraint on the technical-id column
ALTER TABLE public."partitioning"
ADD CONSTRAINT unique_technical_id UNIQUE ("technical-id");

-- Step 1: Add the technical-id column (VARCHAR type, not null)
ALTER TABLE public."source"
ADD COLUMN "technical-id" VARCHAR(10) NOT NULL;

-- Step 2: Update existing rows with a random 's' followed by 4 digits
UPDATE public."source"
SET "technical-id" = 's' || LPAD((floor(random() * 10000)::int)::text, 4, '0');

-- Step 3: Add a unique constraint on the technical-id column
ALTER TABLE public."source"
ADD CONSTRAINT unique_technical_id UNIQUE ("technical-id");

-- Step 1: Add the technical-id column (VARCHAR type, not null)
ALTER TABLE public."survey"
ADD COLUMN "technical-id" VARCHAR(10) NOT NULL;

-- Step 2: Update existing rows with a random 's' followed by 4 digits
UPDATE public."survey"
SET "technical-id" = 's' || LPAD((floor(random() * 10000)::int)::text, 4, '0');

-- Step 3: Add a unique constraint on the technical-id column
ALTER TABLE public."survey"
ADD CONSTRAINT unique_technical_id UNIQUE ("technical-id");