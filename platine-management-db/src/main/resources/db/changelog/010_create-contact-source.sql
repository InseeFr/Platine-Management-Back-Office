--liquibase formatted sql

--changeset davdarras:010-01
CREATE TABLE public.contact_source (
    source_id VARCHAR(255) NOT NULL,
    survey_unit_id VARCHAR(255) NOT NULL,
    contact_id VARCHAR(255) NOT NULL,
    is_main BOOLEAN NOT NULL,
    CONSTRAINT contact_source_pkey PRIMARY KEY (source_id, survey_unit_id, contact_id),
    CONSTRAINT fk_contact_source_source FOREIGN KEY (source_id)
        REFERENCES public."source"(id) ON DELETE CASCADE,
    CONSTRAINT fk_contact_source_survey_unit FOREIGN KEY (survey_unit_id)
        REFERENCES public.survey_unit(id_su) ON DELETE CASCADE,
    CONSTRAINT fk_contact_source_contact FOREIGN KEY (contact_id)
        REFERENCES public.contact(identifier) ON DELETE CASCADE);

CREATE INDEX idx_contact_source_source_survey
    ON public.contact_source (source_id, survey_unit_id);