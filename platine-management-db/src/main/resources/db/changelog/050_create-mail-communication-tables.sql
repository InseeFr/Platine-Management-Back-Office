alter table questioning_communication alter column id SET DEFAULT nextval('questioning_communication_seq');
create table properties_resolver(id bigint GENERATED ALWAYS AS IDENTITY primary key, json_property_resolvers varchar);
create table mail
        (
            id bigint GENERATED ALWAYS AS IDENTITY primary key,
            questioning_id UUID not null references questioning(id),
            template varchar not null,
            mail_subject varchar,
            properties_resolvers_id bigint references properties_resolver(id),
            mail_state varchar,
            to_send_at timestamp,
            sent_at timestamp,
            recipients varchar ARRAY,
            constraint unik_questid_template unique(questioning_id, template, to_send_at)
        );
CREATE INDEX mail_questid_index ON mail USING btree (questioning_id);
CREATE INDEX mail_propresolv_index ON mail USING btree (properties_resolvers_id);
