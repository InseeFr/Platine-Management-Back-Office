--liquibase formatted sql

--changeset bettybecuwe:001_update_owner_support.sql context:prod

ALTER TABLE public."owner" add determiner varchar NULL;
ALTER TABLE public."owner" add signatory_name varchar NULL;
ALTER TABLE public."owner" add signatory_fonction varchar NULL;



ALTER TABLE public."support" add signatory_name varchar NULL;
ALTER TABLE public."support" add signatory_fonction varchar NULL;

ALTER TABLE public."support" add address_line1 varchar NULL;
ALTER TABLE public."support" add address_line2 varchar NULL;
ALTER TABLE public."support" add address_line3 varchar NULL;
ALTER TABLE public."support" add address_line4 varchar NULL;
ALTER TABLE public."support" add address_line5 varchar NULL;
ALTER TABLE public."support" add address_line6 varchar NULL;
ALTER TABLE public."support" add address_line7 varchar NULL;


