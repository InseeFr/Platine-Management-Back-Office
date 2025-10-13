--liquibase formatted sql

--changeset y72wvh:101_update_questioning_communications.sql

ALTER TABLE questioning_communication
    ADD COLUMN with_receipt boolean default false not null,
    ADD COLUMN with_questionnaire boolean default false not null;

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