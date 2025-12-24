--liquibase formatted sql

--changeset davdarras:000_init context:init-db

CREATE TABLE public.address (
	id int8 NOT NULL,
	address_supplement varchar(255) NULL,
	cedex_code varchar(255) NULL,
	cedex_name varchar(255) NULL,
	city_name varchar(255) NULL,
	country_code varchar(255) NULL,
	country_name varchar(255) NULL,
	repetition_index varchar(255) NULL,
	special_distribution varchar(255) NULL,
	street_name varchar(255) NULL,
	street_number varchar(255) NULL,
	street_type varchar(255) NULL,
	zip_code varchar(255) NULL,
	CONSTRAINT address_pkey PRIMARY KEY (id)
);

CREATE TABLE public.event_order (
	id int8 NOT NULL,
	event_order int4 NOT NULL,
	status varchar(255) NULL,
	CONSTRAINT event_order_pkey PRIMARY KEY (id)
);

CREATE TABLE public.internal_users (
	identifier varchar(255) NOT NULL,
	"role" int4 NULL,
	creation_author varchar(255) NULL,
	creation_date timestamp(6) NULL,
	first_name varchar(255) NULL,
	"name" varchar(255) NULL,
	organization varchar(255) NULL,
	CONSTRAINT internal_users_pkey PRIMARY KEY (identifier)
);

CREATE TABLE public.operator_service (
	id int8 NOT NULL,
	mail varchar(255) NULL,
	"name" varchar(255) NULL,
	CONSTRAINT operator_service_pkey PRIMARY KEY (id)
);

CREATE TABLE public."owner" (
	id varchar(255) NOT NULL,
	"label" varchar(255) NULL,
	logo varchar(255) NULL,
	ministry varchar(255) NULL,
	CONSTRAINT owner_pkey PRIMARY KEY (id)
);

CREATE TABLE public.parameters (
	param_id varchar(255) NOT NULL,
	metadata_id varchar(255) NOT NULL,
	param_value varchar(2000) NULL,
	CONSTRAINT parameters_param_id_check CHECK (((param_id)::text = ANY ((ARRAY['URL_REDIRECTION'::character varying, 'URL_TYPE'::character varying, 'MAIL_ASSISTANCE'::character varying])::text[]))),
	CONSTRAINT parameters_pkey PRIMARY KEY (metadata_id, param_id)
);

CREATE TABLE public."support" (
	id varchar(255) NOT NULL,
	city varchar(255) NULL,
	country_name varchar(255) NULL,
	"label" varchar(255) NULL,
	mail varchar(255) NULL,
	phone_number varchar(255) NULL,
	street_name varchar(255) NULL,
	street_number varchar(255) NULL,
	zip_code varchar(255) NULL,
	CONSTRAINT support_pkey PRIMARY KEY (id)
);

CREATE TABLE public.survey_unit_address (
	id int8 NOT NULL,
	address_supplement varchar(255) NULL,
	cedex_code varchar(255) NULL,
	cedex_name varchar(255) NULL,
	city_name varchar(255) NULL,
	country_code varchar(255) NULL,
	country_name varchar(255) NULL,
	repetition_index varchar(255) NULL,
	special_distribution varchar(255) NULL,
	street_name varchar(255) NULL,
	street_number varchar(255) NULL,
	street_type varchar(255) NULL,
	zip_code varchar(255) NULL,
	CONSTRAINT survey_unit_address_pkey PRIMARY KEY (id)
);

CREATE TABLE public.uploads (
	id int8 NOT NULL,
	dateupload int8 NULL,
	CONSTRAINT uploads_pkey PRIMARY KEY (id)
);

CREATE TABLE public."view" (
	id int8 NOT NULL,
	campaign_id varchar(255) NULL,
	id_su varchar(255) NULL,
	identifier varchar(255) NULL,
	CONSTRAINT view_pkey PRIMARY KEY (id)
);
CREATE INDEX view_campaignid_index ON public."view" USING btree (campaign_id);
CREATE INDEX view_identifier_index ON public."view" USING btree (identifier);
CREATE INDEX view_idsu_index ON public."view" USING btree (id_su);

CREATE TABLE public.contact (
	identifier varchar(255) NOT NULL,
	"comment" varchar(255) NULL,
	email varchar(255) NULL,
	email_verify bool DEFAULT false NULL,
	external_id varchar(255) NULL,
	first_name varchar(255) NULL,
	"function" varchar(255) NULL,
	gender varchar(255) NULL,
	last_name varchar(255) NULL,
	phone varchar(255) NULL,
	address_id int8 NULL,
	usual_company_name varchar(255) NULL,
	phone2 varchar(255) NULL,
	CONSTRAINT contact_pkey PRIMARY KEY (identifier),
	CONSTRAINT fkl0sju2uai8cgdtic18wy5hlfr FOREIGN KEY (address_id) REFERENCES public.address(id)
);
CREATE INDEX contactaddress_index ON public.contact USING btree (address_id);
CREATE INDEX email_index ON public.contact USING btree (email);
CREATE INDEX fn_index ON public.contact USING btree (first_name);
CREATE INDEX idx_contact_fullname_upper ON public.contact USING btree (upper((((first_name)::text || ' '::text) || (last_name)::text)));
CREATE INDEX ln_index ON public.contact USING btree (last_name);
CREATE INDEX lnfn_index ON public.contact USING btree (last_name, first_name);

CREATE TABLE public.contact_event (
	id int8 NOT NULL,
	event_date timestamp NULL,
	payload jsonb NULL,
	"type" int4 NULL,
	contact_identifier varchar(255) NULL,
	CONSTRAINT contact_event_pkey PRIMARY KEY (id),
	CONSTRAINT fklac22h764iqxt80tu4g4h284a FOREIGN KEY (contact_identifier) REFERENCES public.contact(identifier)
);

CREATE TABLE public."operator" (
	id int8 NOT NULL,
	first_name varchar(255) NULL,
	last_name varchar(255) NULL,
	phone_number varchar(255) NULL,
	operator_service_id int8 NULL,
	CONSTRAINT operator_pkey PRIMARY KEY (id),
	CONSTRAINT fk3od0favwsbq7f7uuwl9uubm99 FOREIGN KEY (operator_service_id) REFERENCES public.operator_service(id)
);

CREATE TABLE public."source" (
	id varchar(255) NOT NULL,
	long_wording varchar(255) NULL,
	mandatory_my_surveys bool NOT NULL,
	periodicity varchar(255) NULL,
	short_wording varchar(255) NULL,
	owner_id varchar(255) NULL,
	support_id varchar(255) NULL,
	force_close bool NULL,
	message_info_survey_offline varchar(255) NULL,
	message_survey_offline varchar(255) NULL,
	logo varchar(255) NULL,
	CONSTRAINT source_pkey PRIMARY KEY (id),
	CONSTRAINT fkahd906jyo5ysjlpovalcaepda FOREIGN KEY (support_id) REFERENCES public."support"(id),
	CONSTRAINT fkgmojpew8135gresse7lqngpcc FOREIGN KEY (owner_id) REFERENCES public."owner"(id)
);

CREATE TABLE public.source_accreditation (
	id int8 NOT NULL,
	creation_author varchar(255) NULL,
	creation_date timestamp NULL,
	id_user varchar(255) NULL,
	source_id varchar(255) NULL,
	CONSTRAINT source_accreditation_pkey PRIMARY KEY (id),
	CONSTRAINT fk619l4arqi10dpf2fowm1d7ejl FOREIGN KEY (source_id) REFERENCES public."source"(id)
);

CREATE TABLE public.source_params (
	source_id varchar(255) NOT NULL,
	params_metadata_id varchar(255) NOT NULL,
	params_param_id varchar(255) NOT NULL,
	CONSTRAINT source_params_pkey PRIMARY KEY (source_id, params_metadata_id, params_param_id),
	CONSTRAINT ukr3alg3omralfhsdhhuawdv6bp UNIQUE (params_metadata_id, params_param_id),
	CONSTRAINT fk42vqlckna6559sy9vynehi9g FOREIGN KEY (params_metadata_id,params_param_id) REFERENCES public.parameters(metadata_id,param_id),
	CONSTRAINT fklbisi5m1bxuojuo4dv62t4pf1 FOREIGN KEY (source_id) REFERENCES public."source"(id)
);

CREATE TABLE public.survey (
	id varchar(255) NOT NULL,
	cnis_url varchar(255) NULL,
	communication varchar(255) NULL,
	diffusion_url varchar(255) NULL,
	long_objectives varchar(2000) NULL,
	long_wording varchar(2000) NULL,
	notice_url varchar(255) NULL,
	sample_size int4 NULL,
	short_objectives varchar(2000) NULL,
	short_wording varchar(2000) NULL,
	specimen_url varchar(255) NULL,
	visa_number varchar(255) NULL,
	year_value int4 NULL,
	source_id varchar(255) NULL,
	compulsory_nature bool DEFAULT false NULL,
	contact_extraction bool DEFAULT false NULL,
	contact_extraction_nb varchar(255) NULL,
	management_application_name varchar(255) NULL,
	re_expedition bool DEFAULT false NULL,
	rgpd_block varchar(255) NULL,
	send_paper_questionnaire varchar(255) NULL,
	survey_status varchar(255) NULL,
	svi_number varchar(255) NULL,
	svi_use bool DEFAULT false NULL,
	CONSTRAINT survey_pkey PRIMARY KEY (id),
	CONSTRAINT fks3aaf4fow5xmx3l4m5wsmcb6d FOREIGN KEY (source_id) REFERENCES public."source"(id)
);
CREATE INDEX source_index ON public.survey USING btree (source_id);
CREATE INDEX surveyyear_index ON public.survey USING btree (year_value);

CREATE TABLE public.survey_params (
	survey_id varchar(255) NOT NULL,
	params_metadata_id varchar(255) NOT NULL,
	params_param_id varchar(255) NOT NULL,
	CONSTRAINT survey_params_pkey PRIMARY KEY (survey_id, params_metadata_id, params_param_id),
	CONSTRAINT ukbbtfr24diu77x3456vvvqug2k UNIQUE (params_metadata_id, params_param_id),
	CONSTRAINT fk72psmgjcnwekxngauw0e3khl FOREIGN KEY (params_metadata_id,params_param_id) REFERENCES public.parameters(metadata_id,param_id),
	CONSTRAINT fkjxwyrwl318chou4y55oogvj69 FOREIGN KEY (survey_id) REFERENCES public.survey(id)
);

CREATE TABLE public.survey_unit (
	id_su varchar(255) NOT NULL,
	identification_code varchar(255) NULL,
	identification_name varchar(255) NULL,
	survey_unit_address_id int8 NULL,
	"label" varchar(255) NULL,
	CONSTRAINT survey_unit_pkey PRIMARY KEY (id_su),
	CONSTRAINT fkppjrbx0dvc32ro6lpqyfh5n18 FOREIGN KEY (survey_unit_address_id) REFERENCES public.survey_unit_address(id)
);
CREATE INDEX identificationcode_index ON public.survey_unit USING btree (identification_code);
CREATE INDEX identificationname_index ON public.survey_unit USING btree (identification_name);
CREATE INDEX surveyunitaddress_index ON public.survey_unit USING btree (survey_unit_address_id);

CREATE TABLE public.survey_unit_comment (
	id int8 NOT NULL,
	author varchar(255) NULL,
	"comment" varchar(2000) NULL,
	"date" timestamp(6) NULL,
	survey_unit_id_su varchar(255) NULL,
	CONSTRAINT survey_unit_comment_pkey PRIMARY KEY (id),
	CONSTRAINT fketbscimhfndnnd87j2437j2kh FOREIGN KEY (survey_unit_id_su) REFERENCES public.survey_unit(id_su)
);

CREATE TABLE public.user_event (
	id int8 NOT NULL,
	event_date timestamp NULL,
	payload jsonb NULL,
	"type" int4 NULL,
	user_identifier varchar(255) NULL,
	CONSTRAINT user_event_pkey PRIMARY KEY (id),
	CONSTRAINT fk6owot0253wp2wcbrk8vg8twba FOREIGN KEY (user_identifier) REFERENCES public.internal_users(identifier)
);

CREATE TABLE public.campaign (
	id varchar(255) NOT NULL,
	campaign_wording varchar(255) NULL,
	period_value varchar(255) NULL,
	year_value int4 NULL,
	survey_id varchar(255) NULL,
	datacollection_target varchar(255) NULL,
	sensitivity bool DEFAULT false NULL,
	CONSTRAINT campaign_datacollection_target_check CHECK (((datacollection_target)::text = ANY (ARRAY['LUNATIC_SENSITIVE'::text, 'LUNATIC_NORMAL'::text, 'XFORM1'::text, 'XFORM2'::text]))),
	CONSTRAINT campaign_pkey PRIMARY KEY (id),
	CONSTRAINT fk5vdtmadqv1bcng2avs1c66lti FOREIGN KEY (survey_id) REFERENCES public.survey(id)
);
CREATE INDEX surveyid_index ON public.campaign USING btree (survey_id);
CREATE INDEX year_index ON public.campaign USING btree (year_value);

CREATE TABLE public.campaign_event (
	id int8 NOT NULL,
	"date" timestamp NULL,
	"type" varchar(255) NULL,
	campaign_id varchar(255) NULL,
	CONSTRAINT campaign_event_pkey PRIMARY KEY (id),
	CONSTRAINT fkqsavtsdji9ahnpdhy4f0pbusi FOREIGN KEY (campaign_id) REFERENCES public.campaign(id)
);

CREATE TABLE public.campaign_params (
	campaign_id varchar(255) NOT NULL,
	params_metadata_id varchar(255) NOT NULL,
	params_param_id varchar(255) NOT NULL,
	CONSTRAINT campaign_params_pkey PRIMARY KEY (campaign_id, params_metadata_id, params_param_id),
	CONSTRAINT uk4ncdvvwo5mksemb2lqntfhdpq UNIQUE (params_metadata_id, params_param_id),
	CONSTRAINT fki0iosamcbwd7lhsmdxmfdalhq FOREIGN KEY (campaign_id) REFERENCES public.campaign(id),
	CONSTRAINT fkqg4fx1r7rsnkn3ghpfqb9pu5y FOREIGN KEY (params_metadata_id,params_param_id) REFERENCES public.parameters(metadata_id,param_id)
);

CREATE TABLE public.partitioning (
	id varchar(255) NOT NULL,
	closing_date timestamp NULL,
	"label" varchar(255) NULL,
	opening_date timestamp NULL,
	return_date timestamp NULL,
	campaign_id varchar(255) NULL,
	followup_letter_1_date timestamp(6) NULL,
	followup_letter_2_date timestamp(6) NULL,
	followup_letter_3_date timestamp(6) NULL,
	followup_letter_4_date timestamp(6) NULL,
	followup_mail_1_date timestamp(6) NULL,
	followup_mail_2_date timestamp(6) NULL,
	followup_mail_3_date timestamp(6) NULL,
	followup_mail_4_date timestamp(6) NULL,
	formal_notice_date timestamp(6) NULL,
	no_reply_date timestamp(6) NULL,
	opening_letter_date timestamp(6) NULL,
	opening_mail_date timestamp(6) NULL,
	CONSTRAINT partitioning_pkey PRIMARY KEY (id),
	CONSTRAINT fkhh3jep6bbc2nu1fq624pruxb2 FOREIGN KEY (campaign_id) REFERENCES public.campaign(id)
);
CREATE INDEX campainid_index ON public.partitioning USING btree (campaign_id);

CREATE TABLE public.partitioning_params (
	partitioning_id varchar(255) NOT NULL,
	params_metadata_id varchar(255) NOT NULL,
	params_param_id varchar(255) NOT NULL,
	CONSTRAINT partitioning_params_pkey PRIMARY KEY (partitioning_id, params_metadata_id, params_param_id),
	CONSTRAINT ukbmyqvee51p8o5j8c5mh41uwgn UNIQUE (params_metadata_id, params_param_id),
	CONSTRAINT fkdgcag5ysu7roq9kn5i710l3fy FOREIGN KEY (params_metadata_id,params_param_id) REFERENCES public.parameters(metadata_id,param_id),
	CONSTRAINT fksh97u6asr5rg4k9muqjpo16si FOREIGN KEY (partitioning_id) REFERENCES public.partitioning(id)
);

CREATE TABLE public.questioning (
	id int8 NOT NULL,
	id_partitioning varchar(255) NULL,
	model_name varchar(255) NULL,
	survey_unit_id_su varchar(255) NULL,
	CONSTRAINT questioning_pkey PRIMARY KEY (id),
	CONSTRAINT fkhhvsvwkgkkckjafyoe33hlw4h FOREIGN KEY (survey_unit_id_su) REFERENCES public.survey_unit(id_su)
);
CREATE INDEX idpartitioning_index ON public.questioning USING btree (id_partitioning);
CREATE INDEX surveyunitid_index ON public.questioning USING btree (survey_unit_id_su);

CREATE TABLE public.questioning_accreditation (
	id int8 NOT NULL,
	creation_author varchar(255) NULL,
	creation_date timestamp NULL,
	id_contact varchar(255) NULL,
	is_main bool NOT NULL,
	questioning_id int8 NULL,
	CONSTRAINT questioning_accreditation_pkey PRIMARY KEY (id),
	CONSTRAINT fk3yk8aoj5sep1mhgmln7vwu52j FOREIGN KEY (questioning_id) REFERENCES public.questioning(id)
);
CREATE INDEX idcontact_index ON public.questioning_accreditation USING btree (id_contact);
CREATE INDEX questioning_index ON public.questioning_accreditation USING btree (questioning_id);

CREATE TABLE public.questioning_comment (
	id int8 NOT NULL,
	author varchar(255) NULL,
	"comment" varchar(2000) NULL,
	"date" timestamp(6) NULL,
	questioning_id int8 NULL,
	CONSTRAINT questioning_comment_pkey PRIMARY KEY (id),
	CONSTRAINT fk18p09b6mi3mc8stpht63qqgta FOREIGN KEY (questioning_id) REFERENCES public.questioning(id)
);
CREATE INDEX idquestioningcomment_index ON public.questioning_comment USING btree (questioning_id);

CREATE TABLE public.questioning_communication (
	id int8 NOT NULL,
	"date" timestamp(6) NULL,
	status varchar(255) NULL,
	"type" varchar(255) NULL,
	questioning_id int8 NULL,
	CONSTRAINT questioning_communication_pkey PRIMARY KEY (id),
	CONSTRAINT fkrs4r6iv2ckjlqy5xwt5026jqb FOREIGN KEY (questioning_id) REFERENCES public.questioning(id)
);
CREATE INDEX idquestioningcomm_index ON public.questioning_communication USING btree (questioning_id);

CREATE TABLE public.questioning_event (
	id int8 NOT NULL,
	"date" timestamp NULL,
	payload jsonb NULL,
	"type" varchar(255) NULL,
	questioning_id int8 NULL,
	id_upload int8 NULL,
	CONSTRAINT questioning_event_pkey PRIMARY KEY (id),
	CONSTRAINT fk5spfy54450rhf8q3p7r2yy0h8 FOREIGN KEY (id_upload) REFERENCES public.uploads(id),
	CONSTRAINT fkocftpxs551mngv07kghby4laa FOREIGN KEY (questioning_id) REFERENCES public.questioning(id)
);
CREATE INDEX idquestioning_index ON public.questioning_event USING btree (questioning_id);

CREATE SEQUENCE public.address_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.campaign_event_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.contact_event_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.hibernate_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.operator_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.operator_service_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.quest_comment_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.questioning_accreditation_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.questioning_communication_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.questioning_event_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.questioning_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.seq_upload
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.source_accreditation_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.su_comment_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.survey_unit_address_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.user_event_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE SEQUENCE public.view_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

--changeset davdarras:001-02 context:init-db
INSERT INTO public.event_order (id,event_order,status) VALUES
	 (8,8,'REFUSAL'),
	 (7,7,'VALINT'),
	 (6,6,'RECUPAP'),
	 (5,5,'HC'),
	 (4,4,'PARTIELINT'),
	 (3,3,'WASTE'),
	 (2,2,'PND'),
	 (1,1,'INITLA');

