--liquibase formatted sql

--changeset y72wvh:101_update_questioning_communications.sql

ALTER TABLE questioning_communication
    ADD COLUMN with_receipt boolean,
    ADD COLUMN with_questionnaire boolean;

update questioning_communication qc
set with_receipt = false,
with_questionnaire=false;

update questioning_communication qc
set with_receipt = true
where qc."type" = 'COURRIER_CNRAR'
    or qc."type" ='COURRIER_MEDAR';

update questioning_communication qc
set "type" = 'COURRIER_CNR'
where qc."type" = 'COURRIER_CNRAR';

update questioning_communication qc
set "type" = 'COURRIER_MED'
where qc."type" = 'COURRIER_MEDAR';