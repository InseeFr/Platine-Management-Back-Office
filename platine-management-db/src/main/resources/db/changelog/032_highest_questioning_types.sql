--liquibase formatted sql

--changeset davdarras:032-001 context:test-charge

ALTER TABLE public.questioning ADD highest_event_type varchar DEFAULT NULL NULL;
ALTER TABLE public.questioning ADD last_communication_type varchar DEFAULT NULL NULL;
ALTER TABLE public.questioning ADD "validation_date" timestamp NULL;

--changeset davdarras:032-002 context:test-charge

WITH latest_comm AS (
  SELECT DISTINCT ON (qc.questioning_id)
         qc.questioning_id,
         qc.type AS last_comm_type
    FROM questioning_communication qc
   ORDER BY qc.questioning_id,
            qc.date DESC
)
UPDATE questioning q
  SET last_communication_type = lc.last_comm_type
FROM latest_comm lc
WHERE q.id = lc.questioning_id;

WITH highest_evt AS (
  SELECT DISTINCT ON (qe.questioning_id)
         qe.questioning_id,
         qe.type AS highest_event_type
    FROM questioning_event qe
    JOIN interrogation_event_order ie
      ON ie.status = qe.type
   ORDER BY qe.questioning_id,
            ie.event_order DESC,
            qe.date        DESC
)
UPDATE questioning q
  SET highest_event_type = he.highest_event_type
FROM highest_evt he
WHERE q.id = he.questioning_id;

WITH validation_dates AS (
  SELECT DISTINCT ON (qe.questioning_id)
    qe.questioning_id as questioning_id,
    qe.date as validation_date
  FROM questioning_event qe
  WHERE qe.type IN ('VALINT','VALPAP')
  ORDER BY
    qe.questioning_id,
    qe.date DESC
)
UPDATE questioning q
  SET validation_date = vd.validation_date
FROM validation_dates vd
WHERE q.id = vd.questioning_id;