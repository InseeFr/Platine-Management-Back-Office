--changeset davdarras:004-01

CREATE INDEX IF NOT EXISTS idx_contact_fullname_upper ON contact (upper(first_name || ' ' || last_name));