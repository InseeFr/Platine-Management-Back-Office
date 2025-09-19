--liquibase formatted sql

--changeset corbincamille:092_fix_null_gender.sql

-- Mettre à jour toutes les valeurs NULL existantes
UPDATE public.contact
SET gender = 'Undefined'
WHERE gender IS NULL;