--liquibase formatted sql

--changeset j75xzr:160_add_param_paperFormInputEnabled_Source
ALTER TABLE source
    ADD COLUMN paper_form_input_enabled boolean NOT NULL DEFAULT false;