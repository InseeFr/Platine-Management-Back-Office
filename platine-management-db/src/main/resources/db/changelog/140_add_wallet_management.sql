--liquibase formatted sql

--changeset j75xzr:140_add_wallet_management.sql

CREATE TABLE groupe (
    source_id   varchar(255) NOT NULL REFERENCES source(id) ON DELETE CASCADE,
    group_id    uuid NOT NULL,
    label       varchar(255) NOT NULL,
    PRIMARY KEY (source_id, group_id)
);

CREATE TABLE user_group (
    user_id     varchar(255) NOT NULL REFERENCES internal_users(identifier) ON DELETE CASCADE,
    source_id   varchar(255) NOT NULL,
    group_id    uuid NOT NULL,
    FOREIGN KEY (source_id, group_id) REFERENCES groupe(source_id, group_id) ON DELETE CASCADE,
    UNIQUE (user_id, source_id),
    PRIMARY KEY (user_id, source_id, group_id)
);

CREATE TABLE user_wallet (
    survey_unit_id  varchar(255) NOT NULL REFERENCES survey_unit(id_su) ON DELETE CASCADE,
    source_id       varchar(255) NOT NULL REFERENCES source(id) ON DELETE CASCADE,
    user_id         varchar(255) NOT NULL REFERENCES internal_users(identifier) ON DELETE CASCADE,
    UNIQUE (survey_unit_id, source_id),
    FOREIGN KEY (user_id, source_id)
        REFERENCES user_group (user_id, source_id)
        ON DELETE RESTRICT,
    PRIMARY KEY (survey_unit_id, source_id, user_id)
);

CREATE TABLE group_wallet (
    survey_unit_id  varchar(255) NOT NULL REFERENCES survey_unit(id_su) ON DELETE CASCADE,
    source_id       varchar(255) NOT NULL,
    group_id        uuid NOT NULL,
    FOREIGN KEY (source_id, group_id)
        REFERENCES groupe(source_id, group_id)
        ON DELETE CASCADE,
    PRIMARY KEY (source_id, group_id, survey_unit_id)
);

CREATE INDEX idx_groupe_source ON groupe(source_id);
CREATE INDEX idx_user_group_source ON user_group(source_id);
CREATE INDEX idx_user_wallet_source ON user_wallet(source_id);
CREATE INDEX idx_group_wallet_group ON group_wallet (source_id, group_id);
CREATE INDEX idx_group_wallet_su ON group_wallet (survey_unit_id);
CREATE INDEX idx_user_wallet_user ON user_wallet (user_id, source_id);