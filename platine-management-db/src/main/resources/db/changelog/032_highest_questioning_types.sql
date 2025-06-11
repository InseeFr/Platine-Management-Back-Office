--liquibase formatted sql

--changeset davdarras:032-001 context:test-charge

ALTER TABLE public.questioning ADD highest_event_type varchar DEFAULT NULL NULL;
ALTER TABLE public.questioning ADD latest_communication_type varchar DEFAULT NULL NULL;

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
  SET latest_communication_type = lc.last_comm_type
FROM latest_comm lc
WHERE q.id = lc.questioning_id;

WITH latest_evt AS (
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
  SET highest_event_type = le.highest_event_type
FROM latest_evt le
WHERE q.id = le.questioning_id;