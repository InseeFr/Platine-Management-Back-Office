--liquibase formatted sql

--changeset davdarras:060-01

ALTER TABLE questioning_accreditation DROP CONSTRAINT fk3yk8aoj5sep1mhgmln7vwu52j;
ALTER TABLE questioning_comment DROP CONSTRAINT fk18p09b6mi3mc8stpht63qqgta;
ALTER TABLE questioning_communication DROP CONSTRAINT fkrs4r6iv2ckjlqy5xwt5026jqb;
ALTER TABLE questioning_event DROP CONSTRAINT fkocftpxs551mngv07kghby4laa;
ALTER TABLE questioning DROP CONSTRAINT questioning_pkey;
ALTER TABLE mail DROP CONSTRAINT mail_questioning_id_fkey;

ALTER TABLE questioning RENAME COLUMN id TO old_id;
ALTER TABLE public.questioning ALTER COLUMN old_id DROP NOT NULL;
ALTER TABLE questioning RENAME COLUMN interrogation_id TO id;

ALTER TABLE questioning_comment RENAME COLUMN questioning_id TO questioning_old_id;
ALTER TABLE questioning_comment ADD COLUMN questioning_id UUID;
UPDATE questioning_comment
	SET questioning_id = q.id
	FROM questioning q
	WHERE q.old_id = questioning_comment.questioning_old_id;

ALTER TABLE questioning_communication RENAME COLUMN questioning_id TO questioning_old_id;
ALTER TABLE questioning_communication ADD COLUMN questioning_id UUID;
update questioning_communication set questioning_id = q.id from questioning q where q.old_id = questioning_old_id;

ALTER TABLE questioning_event RENAME COLUMN questioning_id TO questioning_old_id;
ALTER TABLE questioning_event ADD COLUMN questioning_id UUID;
update questioning_event set questioning_id = q.id from questioning q where q.old_id = questioning_old_id;

ALTER TABLE questioning_accreditation RENAME COLUMN questioning_id TO questioning_old_id;
ALTER TABLE questioning_accreditation ADD COLUMN questioning_id UUID;
update questioning_accreditation set questioning_id = q.id from questioning q where q.old_id = questioning_old_id;

ALTER TABLE mail RENAME COLUMN questioning_id TO questioning_old_id;
ALTER TABLE mail ADD COLUMN questioning_id UUID;
update mail set questioning_id = q.id from questioning q where q.old_id = questioning_old_id;

ALTER TABLE questioning ADD CONSTRAINT questioning_pkey PRIMARY KEY (id);

ALTER TABLE questioning_accreditation
  ADD CONSTRAINT questioning_accreditation_questioning_id_fkey
  FOREIGN KEY (questioning_id)
  REFERENCES questioning(id)
  ON DELETE CASCADE;

ALTER TABLE questioning_comment
  ADD CONSTRAINT questioning_comment_questioning_id_fkey
  FOREIGN KEY (questioning_id)
  REFERENCES questioning(id)
  ON DELETE CASCADE;

ALTER TABLE questioning_event
  ADD CONSTRAINT questioning_event_questioning_id_fkey
  FOREIGN KEY (questioning_id)
  REFERENCES questioning(id)
  ON DELETE CASCADE;

ALTER TABLE questioning_communication
  ADD CONSTRAINT questioning_communication_questioning_id_fkey
  FOREIGN KEY (questioning_id)
  REFERENCES questioning(id)
  ON DELETE CASCADE;

ALTER TABLE mail
  ADD CONSTRAINT mail_questioning_id_fkey
  FOREIGN KEY (questioning_id)
  REFERENCES questioning(id);

ALTER TABLE questioning_accreditation DROP COLUMN questioning_old_id;
ALTER TABLE questioning_comment DROP COLUMN questioning_old_id;
ALTER TABLE questioning_event DROP COLUMN questioning_old_id;
ALTER TABLE questioning_communication DROP COLUMN questioning_old_id;
ALTER TABLE mail DROP COLUMN questioning_old_id;
