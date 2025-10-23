--liquibase formatted sql

--changeset davdarras:101_create_survey_unit_event:001

CREATE SEQUENCE IF NOT EXISTS survey_unit_event_seq
    INCREMENT BY 50
    MINVALUE 1
    START WITH 1
    CACHE 50;

CREATE TABLE IF NOT EXISTS survey_unit_event (
    id               BIGINT PRIMARY KEY DEFAULT nextval('survey_unit_event_seq'),
    survey_unit_id   varchar(255) NOT NULL,
    campaign_id      varchar(255) NOT NULL,
    "date"           TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    creation_date    TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    type             VARCHAR(64) NOT NULL,
    source           VARCHAR(64) NOT NULL
);

ALTER TABLE survey_unit_event ADD CONSTRAINT fk_sue_survey_unit
                    FOREIGN KEY (survey_unit_id) REFERENCES survey_unit (id_su);

ALTER TABLE survey_unit_event ADD CONSTRAINT fk_sue_campaign
                    FOREIGN KEY (campaign_id) REFERENCES campaign (id);

ALTER SEQUENCE survey_unit_event_seq OWNED BY survey_unit_event.id;

CREATE INDEX IF NOT EXISTS idx_sue_survey_unit_date
    ON survey_unit_event (survey_unit_id, "date" DESC);

CREATE INDEX IF NOT EXISTS idx_sue_campaign
    ON survey_unit_event (campaign_id);