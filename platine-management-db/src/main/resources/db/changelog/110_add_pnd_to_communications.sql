--liquibase formatted sql

--changeset y72wvh:110_add_pnd_to_communications.sql

INSERT INTO questioning_communication (date, status, type, questioning_id)
SELECT
    qe.date,
    'AUTOMATIC',
    qe.type,
    qe.questioning_id
FROM
    questioning_event qe
LEFT JOIN
    questioning_communication qc ON qe.questioning_id = qc.questioning_id AND qe.type = qc.type
WHERE
    qe.type = 'PND' AND qc.id IS NULL; -- only if not exists
    qe.type = 'PND' AND qc.id IS NULL; -- only if not exists

