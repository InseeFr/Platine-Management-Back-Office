--liquibase formatted sql

--changeset bettybecuwe:011-01
drop table if exists support_sources ;
drop table if exists owner_sources;

ALTER TABLE public."support" DROP COLUMN city;
ALTER TABLE public."support" DROP COLUMN country_name;
ALTER TABLE public."support" DROP COLUMN street_name;
ALTER TABLE public."support" DROP COLUMN street_number;
ALTER TABLE public."support" DROP COLUMN zip_code;
