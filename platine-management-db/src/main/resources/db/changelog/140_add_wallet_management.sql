--liquibase formatted sql

--changeset j75xzr:140_add_wallet_management.sql

CREATE TABLE groups (
    source_id   varchar(255) NOT NULL REFERENCES source(id) ON DELETE CASCADE,
    group_id    uuid NOT NULL,
    label       varchar(255) NOT NULL,
    CONSTRAINT unik_source_group_label UNIQUE(source_id, label),
    CONSTRAINT group_pkey PRIMARY KEY (group_id)
);

CREATE TABLE user_group (
    group_id    uuid NOT NULL REFERENCES groups(group_id) ON DELETE CASCADE,
    user_id     varchar(255) NOT NULL REFERENCES internal_users(identifier) ON DELETE CASCADE,
    CONSTRAINT user_group_pkey PRIMARY KEY (user_id, group_id)
);

CREATE TABLE user_wallet (
    user_id   varchar(255) NOT NULL REFERENCES internal_users(identifier) ON DELETE CASCADE,
    survey_unit_id  varchar(255) NOT NULL REFERENCES survey_unit(id_su) ON DELETE CASCADE,
    source_id   varchar(255) NOT NULL REFERENCES source(id) ON DELETE CASCADE,
    CONSTRAINT user_wallet_pkey PRIMARY KEY (user_id, survey_unit_id, source_id)
);

CREATE TABLE group_wallet (
    group_id        uuid NOT NULL REFERENCES groups(group_id) ON DELETE CASCADE,
    survey_unit_id  varchar(255) NOT NULL REFERENCES survey_unit(id_su) ON DELETE CASCADE,
    PRIMARY KEY (group_id, survey_unit_id)
);

CREATE INDEX idx_user_group_group_id ON user_group(group_id);
CREATE INDEX idx_user_group_user_id ON user_group(user_id);
CREATE INDEX idx_user_wallet_su ON user_wallet (survey_unit_id);
CREATE INDEX idx_group_wallet_su ON group_wallet (survey_unit_id);
CREATE INDEX idx_groupe_source ON groups(source_id);
CREATE INDEX idx_user_wallet_source ON user_wallet (source_id);
CREATE INDEX idx_user_wallet_user_source ON user_wallet (user_id, source_id);
CREATE INDEX idx_user_wallet_source_su ON user_wallet (source_id, survey_unit_id);