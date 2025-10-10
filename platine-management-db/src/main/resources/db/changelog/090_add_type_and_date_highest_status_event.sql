--liquibase formatted sql

--changeset j75xzr:090_add_type_and_date_highest_status_event.sql

ALTER TABLE questioning
    ADD COLUMN highest_event_type varchar,
    ADD COLUMN highest_event_date timestamp;

    ALTER TABLE questioning_communication
        ADD COLUMN with_receipt boolean,
        ADD COLUMN with_questionnaire boolean;

    update questioning_communication qc
    set with_receipt = false, with_questionnaire=false;.alter

    update questioning_communication qc
    set with_receipt = true
    where qc."type" = 'COURRIER_CNRAR' or qc."type" ='COURRIER_MEDAR';



    update questioning_communication qc
    set "type" = 'COURRIER_CNR'
    where qc."type" = 'COURRIER_CNRAR';

    update questioning_communication qc
    set "type" = 'COURRIER_MED'
    where qc."type" = 'COURRIER_MEDAR';