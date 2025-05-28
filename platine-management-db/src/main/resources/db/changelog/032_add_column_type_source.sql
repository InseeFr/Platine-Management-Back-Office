--liquibase formatted sql

--changeset prwozny:032-01 add_column_type_source.sql

ALTER TABLE public.source add type varchar NULL;
