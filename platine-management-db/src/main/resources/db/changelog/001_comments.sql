--liquibase formatted sql

--changeset davdarras:001-01

ALTER TABLE public.questioning_comment
    ADD COLUMN questioning_id int8
    REFERENCES public.questioning(id);

ALTER TABLE public.questioning_comment
    ADD CONSTRAINT fk18p09b6mi3mc8stpht63qqgta
    FOREIGN KEY (questioning_id)
    REFERENCES public.questioning(id);

ALTER TABLE public.survey_unit_comment
    ADD COLUMN survey_unit_id_su varchar(255) NULL
    REFERENCES public.survey_unit(id_su);

ALTER TABLE public.survey_unit_comment
    ADD CONSTRAINT fketbscimhfndnnd87j2437j2kh
    FOREIGN KEY (survey_unit_id_su)
    REFERENCES public.survey_unit(id_su);