--liquibase formatted sql

--changeset corbincamille:092_fix_null_gender.sql

-- Mettre à jour toutes les valeurs NULL existantes
UPDATE public.contact
SET gender = 'UNDEFINED'
WHERE gender IS NULL;

-- Harmoniser les anciennes valeurs avec la nouvelle convention en majuscules
UPDATE public.contact SET gender = 'FEMALE' WHERE gender ILIKE 'female';
UPDATE public.contact SET gender = 'MALE' WHERE gender ILIKE 'male';
UPDATE public.contact SET gender = 'UNDEFINED' WHERE gender ILIKE 'undefined';