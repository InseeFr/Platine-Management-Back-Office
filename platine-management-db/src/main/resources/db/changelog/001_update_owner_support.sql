--liquibase formatted sql

--changeset bettybecuwe:001_update_owner_support.sql

ALTER TABLE public."owner" add determiner varchar NULL;
ALTER TABLE public."owner" add signatory_name varchar NULL;
ALTER TABLE public."owner" add signatory_fonction varchar NULL;



ALTER TABLE public."support" add signatory_name varchar NULL;
ALTER TABLE public."support" add signatory_fonction varchar NULL;

ALTER TABLE public."support" add address_line_1 varchar NULL;
ALTER TABLE public."support" add address_line_2 varchar NULL;
ALTER TABLE public."support" add address_line_3 varchar NULL;
ALTER TABLE public."support" add address_line_4 varchar NULL;
ALTER TABLE public."support" add address_line_5 varchar NULL;
ALTER TABLE public."support" add address_line_6 varchar NULL;
ALTER TABLE public."support" add address_line_7 varchar NULL;

ALTER TABLE public."source" add storage_time varchar NULL;
ALTER TABLE public."source" add personal_data varchar NULL;


