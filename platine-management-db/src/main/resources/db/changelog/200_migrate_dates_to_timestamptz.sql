--liquibase formatted sql

-- ============================================================
-- Migration : colonnes DATE/TIMESTAMP → TIMESTAMPTZ
-- Les données existantes sont interprétées comme UTC
-- ============================================================

-- changeset ddarras:200-01 context:prod
-- Table : questioning_event — colonne "date"
ALTER TABLE questioning_event
    ALTER COLUMN "date" TYPE timestamptz
    USING "date" AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE questioning_event ALTER COLUMN "date" TYPE timestamp WITHOUT TIME ZONE USING "date" AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-02 context:prod
-- Table : user_event — colonne event_date
ALTER TABLE user_event
    ALTER COLUMN event_date TYPE timestamptz
    USING event_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE user_event ALTER COLUMN event_date TYPE timestamp WITHOUT TIME ZONE USING event_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-03 context:prod
-- Table : contact_event — colonne event_date
ALTER TABLE contact_event
    ALTER COLUMN event_date TYPE timestamptz
    USING event_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE contact_event ALTER COLUMN event_date TYPE timestamp WITHOUT TIME ZONE USING event_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-04 context:prod
-- Table : questioning — colonne highest_event_date
ALTER TABLE questioning
    ALTER COLUMN highest_event_date TYPE timestamptz
    USING highest_event_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE questioning ALTER COLUMN highest_event_date TYPE timestamp WITHOUT TIME ZONE USING highest_event_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-05 context:prod
-- Table : questioning_accreditation — colonne creation_date
ALTER TABLE questioning_accreditation
    ALTER COLUMN creation_date TYPE timestamptz
    USING creation_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE questioning_accreditation ALTER COLUMN creation_date TYPE timestamp WITHOUT TIME ZONE USING creation_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-06 context:prod
-- Table : questioning_comment — colonne "date"
ALTER TABLE questioning_comment
    ALTER COLUMN "date" TYPE timestamptz
    USING "date" AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE questioning_comment ALTER COLUMN "date" TYPE timestamp WITHOUT TIME ZONE USING "date" AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-07 context:prod
-- Table : survey_unit_comment — colonne "date"
ALTER TABLE survey_unit_comment
    ALTER COLUMN "date" TYPE timestamptz
    USING "date" AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE survey_unit_comment ALTER COLUMN "date" TYPE timestamp WITHOUT TIME ZONE USING "date" AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-08 context:prod
-- Table : partitioning — ouverture/fermeture/retour
ALTER TABLE partitioning
    ALTER COLUMN opening_date TYPE timestamptz
    USING opening_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN closing_date TYPE timestamptz
    USING closing_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN return_date TYPE timestamptz
    USING return_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE partitioning ALTER COLUMN opening_date TYPE timestamp WITHOUT TIME ZONE USING opening_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN closing_date TYPE timestamp WITHOUT TIME ZONE USING closing_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN return_date TYPE timestamp WITHOUT TIME ZONE USING return_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-09 context:prod
-- Table : partitioning — courrier/mail ouverture
ALTER TABLE partitioning
    ALTER COLUMN opening_letter_date TYPE timestamptz
    USING opening_letter_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN opening_mail_date TYPE timestamptz
    USING opening_mail_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE partitioning ALTER COLUMN opening_letter_date TYPE timestamp WITHOUT TIME ZONE USING opening_letter_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN opening_mail_date TYPE timestamp WITHOUT TIME ZONE USING opening_mail_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-10 context:prod
-- Table : partitioning — relances courrier 1-4
ALTER TABLE partitioning
    ALTER COLUMN followup_letter_1_date TYPE timestamptz
    USING followup_letter_1_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN followup_letter_2_date TYPE timestamptz
    USING followup_letter_2_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN followup_letter_3_date TYPE timestamptz
    USING followup_letter_3_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN followup_letter_4_date TYPE timestamptz
    USING followup_letter_4_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE partitioning ALTER COLUMN followup_letter_1_date TYPE timestamp WITHOUT TIME ZONE USING followup_letter_1_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN followup_letter_2_date TYPE timestamp WITHOUT TIME ZONE USING followup_letter_2_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN followup_letter_3_date TYPE timestamp WITHOUT TIME ZONE USING followup_letter_3_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN followup_letter_4_date TYPE timestamp WITHOUT TIME ZONE USING followup_letter_4_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-11 context:prod
-- Table : partitioning — relances mail 1-4
ALTER TABLE partitioning
    ALTER COLUMN followup_mail_1_date TYPE timestamptz
    USING followup_mail_1_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN followup_mail_2_date TYPE timestamptz
    USING followup_mail_2_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN followup_mail_3_date TYPE timestamptz
    USING followup_mail_3_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN followup_mail_4_date TYPE timestamptz
    USING followup_mail_4_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE partitioning ALTER COLUMN followup_mail_1_date TYPE timestamp WITHOUT TIME ZONE USING followup_mail_1_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN followup_mail_2_date TYPE timestamp WITHOUT TIME ZONE USING followup_mail_2_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN followup_mail_3_date TYPE timestamp WITHOUT TIME ZONE USING followup_mail_3_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN followup_mail_4_date TYPE timestamp WITHOUT TIME ZONE USING followup_mail_4_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-12 context:prod
-- Table : partitioning — mise en demeure / sans réponse
ALTER TABLE partitioning
    ALTER COLUMN formal_notice_date TYPE timestamptz
    USING formal_notice_date AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN no_reply_date TYPE timestamptz
    USING no_reply_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE partitioning ALTER COLUMN formal_notice_date TYPE timestamp WITHOUT TIME ZONE USING formal_notice_date AT TIME ZONE 'Europe/Paris', ALTER COLUMN no_reply_date TYPE timestamp WITHOUT TIME ZONE USING no_reply_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-13 context:prod
-- Table : survey_unit_event — colonnes date et creation_date (LOCAL → TZ)
ALTER TABLE survey_unit_event
    ALTER COLUMN "date" TYPE timestamptz
    USING "date" AT TIME ZONE 'Europe/Paris',
    ALTER COLUMN creation_date TYPE timestamptz
    USING creation_date AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE survey_unit_event ALTER COLUMN "date" TYPE timestamp WITHOUT TIME ZONE USING "date" AT TIME ZONE 'Europe/Paris', ALTER COLUMN creation_date TYPE timestamp WITHOUT TIME ZONE USING creation_date AT TIME ZONE 'Europe/Paris';

-- changeset ddarras:200-14 context:prod
-- Table : questioning_communication — colonne "date" (LOCAL → TZ)
ALTER TABLE questioning_communication
    ALTER COLUMN "date" TYPE timestamptz
    USING "date" AT TIME ZONE 'Europe/Paris';
-- rollback ALTER TABLE questioning_communication ALTER COLUMN "date" TYPE timestamp WITHOUT TIME ZONE USING "date" AT TIME ZONE 'Europe/Paris';
