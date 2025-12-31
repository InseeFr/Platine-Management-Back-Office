--liquibase formatted sql

--changeset pgpu0i:180_add_status_for_questioning_event dbms:postgresql
ALTER TABLE questioning_event

    ADD COLUMN IF NOT EXISTS status VARCHAR(255);

UPDATE questioning_event
SET status = CASE
                 WHEN type IN ('HC', 'WASTE', 'REFUSAL', 'VALPAP') THEN 'MANUAL'
                 ELSE 'AUTOMATIC'
    END
WHERE status IS NULL;