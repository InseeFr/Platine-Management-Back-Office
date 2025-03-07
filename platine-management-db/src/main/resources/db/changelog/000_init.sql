--liquibase formatted sql

--changeset davdarras:000_init context:init-db

-- public.address definition
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


-- public.event_order definition
CREATE TABLE public.event_order (
	id int8 NOT NULL,
	event_order int4 NOT NULL,
	status varchar(255) NULL,
	CONSTRAINT event_order_pkey PRIMARY KEY (id)
);

-- public.internal_users definition
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


-- public."operator" definition
CREATE TABLE public."operator" (
	id int8 NOT NULL,
	first_name varchar(255) NULL,
	last_name varchar(255) NULL,
	phone_number varchar(255) NULL,
	CONSTRAINT operator_pkey PRIMARY KEY (id)
);


-- public.operator_service definition
CREATE TABLE public.operator_service (
	id int8 NOT NULL,
	mail varchar(255) NULL,
	"name" varchar(255) NULL,
	CONSTRAINT operator_service_pkey PRIMARY KEY (id)
);


-- public."owner" definition
CREATE TABLE public."owner" (
	id varchar(255) NOT NULL,
	"label" varchar(255) NULL,
	logo varchar(255) NULL,
	ministry varchar(255) NULL,
	CONSTRAINT owner_pkey PRIMARY KEY (id)
);


-- public.parameters definition
CREATE TABLE public.parameters (
	param_id varchar(255) NOT NULL,
	metadata_id varchar(255) NOT NULL,
	param_value varchar(2000) NULL,
	CONSTRAINT parameters_param_id_check CHECK (((param_id)::text = ANY ((ARRAY['URL_REDIRECTION'::character varying, 'URL_TYPE'::character varying, 'MAIL_ASSISTANCE'::character varying])::text[]))),
	CONSTRAINT parameters_pkey PRIMARY KEY (metadata_id, param_id)
);


-- public.questioning_comment definition
CREATE TABLE public.questioning_comment (
	id int8 NOT NULL,
	author varchar(255) NULL,
	"comment" varchar(2000) NULL,
	"date" timestamp(6) NULL,
	CONSTRAINT questioning_comment_pkey PRIMARY KEY (id)
);


-- public."support" definition
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


-- public.survey_unit_address definition
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


-- public.survey_unit_comment definition
CREATE TABLE public.survey_unit_comment (
	id int8 NOT NULL,
	author varchar(255) NULL,
	"comment" varchar(2000) NULL,
	"date" timestamp(6) NULL,
	CONSTRAINT survey_unit_comment_pkey PRIMARY KEY (id)
);


-- public.uploads definition
CREATE TABLE public.uploads (
	id int8 NOT NULL,
	dateupload int8 NULL,
	CONSTRAINT uploads_pkey PRIMARY KEY (id)
);


-- public."view" definition
CREATE TABLE public."view" (
	id int8 NOT NULL,
	campaign_id varchar(255) NULL,
	id_su varchar(255) NULL,
	identifier varchar(255) NULL,
	CONSTRAINT view_pkey PRIMARY KEY (id)
);
CREATE INDEX view_campaignid_index ON public.view USING btree (campaign_id);
CREATE INDEX view_identifier_index ON public.view USING btree (identifier);
CREATE INDEX view_idsu_index ON public.view USING btree (id_su);


-- public.contact definition
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
CREATE INDEX ln_index ON public.contact USING btree (last_name);
CREATE INDEX lnfn_index ON public.contact USING btree (last_name, first_name);


-- public.contact_event definition
CREATE TABLE public.contact_event (
	id int8 NOT NULL,
	event_date timestamp NULL,
	payload jsonb NULL,
	"type" int4 NULL,
	contact_identifier varchar(255) NULL,
	CONSTRAINT contact_event_pkey PRIMARY KEY (id),
	CONSTRAINT fklac22h764iqxt80tu4g4h284a FOREIGN KEY (contact_identifier) REFERENCES public.contact(identifier)
);


-- public.operator_service_operators definition
CREATE TABLE public.operator_service_operators (
	operator_service_id int8 NOT NULL,
	operators_id int8 NOT NULL,
	CONSTRAINT operator_service_operators_pkey PRIMARY KEY (operator_service_id, operators_id),
	CONSTRAINT uk_i3aieo7c6q4nmo8uysu9fa8jr UNIQUE (operators_id),
	CONSTRAINT fk18ukjypdwq23toiuhpd0d4w68 FOREIGN KEY (operator_service_id) REFERENCES public.operator_service(id),
	CONSTRAINT fk4389uy4v51fgtg6owtvf0n9v0 FOREIGN KEY (operators_id) REFERENCES public."operator"(id)
);


-- public."source" definition
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


-- public.source_accreditation definition
CREATE TABLE public.source_accreditation (
	id int8 NOT NULL,
	creation_author varchar(255) NULL,
	creation_date timestamp NULL,
	id_user varchar(255) NULL,
	source_id varchar(255) NULL,
	CONSTRAINT source_accreditation_pkey PRIMARY KEY (id),
	CONSTRAINT fk619l4arqi10dpf2fowm1d7ejl FOREIGN KEY (source_id) REFERENCES public."source"(id)
);


-- public.source_params definition
CREATE TABLE public.source_params (
	source_id varchar(255) NOT NULL,
	params_metadata_id varchar(255) NOT NULL,
	params_param_id varchar(255) NOT NULL,
	CONSTRAINT source_params_pkey PRIMARY KEY (source_id, params_metadata_id, params_param_id),
	CONSTRAINT ukr3alg3omralfhsdhhuawdv6bp UNIQUE (params_metadata_id, params_param_id),
	CONSTRAINT fk42vqlckna6559sy9vynehi9g FOREIGN KEY (params_metadata_id,params_param_id) REFERENCES public.parameters(metadata_id,param_id),
	CONSTRAINT fklbisi5m1bxuojuo4dv62t4pf1 FOREIGN KEY (source_id) REFERENCES public."source"(id)
);


-- public.source_source_accreditations definition
CREATE TABLE public.source_source_accreditations (
	source_id varchar(255) NOT NULL,
	source_accreditations_id int8 NOT NULL,
	CONSTRAINT source_source_accreditations_pkey PRIMARY KEY (source_id, source_accreditations_id),
	CONSTRAINT uk_ipsob2c2jlc9qefs3kselcoc9 UNIQUE (source_accreditations_id),
	CONSTRAINT fkfda152qwgkkx0kbxvdqu4uk2y FOREIGN KEY (source_accreditations_id) REFERENCES public.source_accreditation(id),
	CONSTRAINT fkgergfheti3520uc4a5vhch8u4 FOREIGN KEY (source_id) REFERENCES public."source"(id)
);


-- public.support_sources definition
CREATE TABLE public.support_sources (
	support_id varchar(255) NOT NULL,
	sources_id varchar(255) NOT NULL,
	CONSTRAINT support_sources_pkey PRIMARY KEY (support_id, sources_id),
	CONSTRAINT uk_b5jwvjfcqm2p8t3sr076a4m35 UNIQUE (sources_id),
	CONSTRAINT fk7du4ksu8ri8dr49ffbsid7axa FOREIGN KEY (sources_id) REFERENCES public."source"(id),
	CONSTRAINT fkgpvyjdej7tc6deqmejrf36s9l FOREIGN KEY (support_id) REFERENCES public."support"(id)
);


-- public.survey definition
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
	CONSTRAINT survey_pkey PRIMARY KEY (id),
	CONSTRAINT fks3aaf4fow5xmx3l4m5wsmcb6d FOREIGN KEY (source_id) REFERENCES public."source"(id)
);
CREATE INDEX source_index ON public.survey USING btree (source_id);
CREATE INDEX surveyyear_index ON public.survey USING btree (year_value);


-- public.survey_params definition
CREATE TABLE public.survey_params (
	survey_id varchar(255) NOT NULL,
	params_metadata_id varchar(255) NOT NULL,
	params_param_id varchar(255) NOT NULL,
	CONSTRAINT survey_params_pkey PRIMARY KEY (survey_id, params_metadata_id, params_param_id),
	CONSTRAINT ukbbtfr24diu77x3456vvvqug2k UNIQUE (params_metadata_id, params_param_id),
	CONSTRAINT fk72psmgjcnwekxngauw0e3khl FOREIGN KEY (params_metadata_id,params_param_id) REFERENCES public.parameters(metadata_id,param_id),
	CONSTRAINT fkjxwyrwl318chou4y55oogvj69 FOREIGN KEY (survey_id) REFERENCES public.survey(id)
);


-- public.survey_unit definition
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


-- public.survey_unit_survey_unit_comments definition
CREATE TABLE public.survey_unit_survey_unit_comments (
	survey_unit_id_su varchar(255) NOT NULL,
	survey_unit_comments_id int8 NOT NULL,
	CONSTRAINT survey_unit_survey_unit_comments_pkey PRIMARY KEY (survey_unit_id_su, survey_unit_comments_id),
	CONSTRAINT uklpkrv9on19rkokc843ylo7676 UNIQUE (survey_unit_comments_id),
	CONSTRAINT fknnu0mu5hruvkus9v0h9nw5dii FOREIGN KEY (survey_unit_comments_id) REFERENCES public.survey_unit_comment(id),
	CONSTRAINT fkqxrv1i9i6j33s2u03cv9bnc4t FOREIGN KEY (survey_unit_id_su) REFERENCES public.survey_unit(id_su)
);


-- public.user_event definition
CREATE TABLE public.user_event (
	id int8 NOT NULL,
	event_date timestamp NULL,
	payload jsonb NULL,
	"type" int4 NULL,
	user_identifier varchar(255) NULL,
	CONSTRAINT user_event_pkey PRIMARY KEY (id),
	CONSTRAINT fk6owot0253wp2wcbrk8vg8twba FOREIGN KEY (user_identifier) REFERENCES public.internal_users(identifier)
);


-- public.campaign definition
CREATE TABLE public.campaign (
	id varchar(255) NOT NULL,
	campaign_wording varchar(255) NULL,
	period_value varchar(255) NULL,
	year_value int4 NULL,
	survey_id varchar(255) NULL,
	CONSTRAINT campaign_pkey PRIMARY KEY (id),
	CONSTRAINT fk5vdtmadqv1bcng2avs1c66lti FOREIGN KEY (survey_id) REFERENCES public.survey(id)
);
CREATE INDEX surveyid_index ON public.campaign USING btree (survey_id);
CREATE INDEX year_index ON public.campaign USING btree (year_value);


-- public.campaign_event definition
CREATE TABLE public.campaign_event (
	id int8 NOT NULL,
	"date" timestamp NULL,
	"type" varchar(255) NULL,
	campaign_id varchar(255) NULL,
	CONSTRAINT campaign_event_pkey PRIMARY KEY (id),
	CONSTRAINT fkqsavtsdji9ahnpdhy4f0pbusi FOREIGN KEY (campaign_id) REFERENCES public.campaign(id)
);


-- public.campaign_params definition
CREATE TABLE public.campaign_params (
	campaign_id varchar(255) NOT NULL,
	params_metadata_id varchar(255) NOT NULL,
	params_param_id varchar(255) NOT NULL,
	CONSTRAINT campaign_params_pkey PRIMARY KEY (campaign_id, params_metadata_id, params_param_id),
	CONSTRAINT uk4ncdvvwo5mksemb2lqntfhdpq UNIQUE (params_metadata_id, params_param_id),
	CONSTRAINT fki0iosamcbwd7lhsmdxmfdalhq FOREIGN KEY (campaign_id) REFERENCES public.campaign(id),
	CONSTRAINT fkqg4fx1r7rsnkn3ghpfqb9pu5y FOREIGN KEY (params_metadata_id,params_param_id) REFERENCES public.parameters(metadata_id,param_id)
);


-- public.contact_contact_events definition
CREATE TABLE public.contact_contact_events (
	contact_identifier varchar(255) NOT NULL,
	contact_events_id int8 NOT NULL,
	CONSTRAINT contact_contact_events_pkey PRIMARY KEY (contact_identifier, contact_events_id),
	CONSTRAINT uk_mrx72yd6mqc25m62yg4kc7fdb UNIQUE (contact_events_id),
	CONSTRAINT fkkn3ud44310eil6comq4thkag4 FOREIGN KEY (contact_identifier) REFERENCES public.contact(identifier),
	CONSTRAINT fkm5lkar6f1k5ob3xyak46fh3um FOREIGN KEY (contact_events_id) REFERENCES public.contact_event(id)
);


-- public.internal_users_user_events definition
CREATE TABLE public.internal_users_user_events (
	user_identifier varchar(255) NOT NULL,
	user_events_id int8 NOT NULL,
	CONSTRAINT internal_users_user_events_pkey PRIMARY KEY (user_identifier, user_events_id),
	CONSTRAINT uk_avcasxflfkw3006559ue02lqt UNIQUE (user_events_id),
	CONSTRAINT fklnkk0xjxlqqrjw5maqfp7utas FOREIGN KEY (user_events_id) REFERENCES public.user_event(id),
	CONSTRAINT fkohymm8pgiuvhvgq6a0hvt8a5k FOREIGN KEY (user_identifier) REFERENCES public.internal_users(identifier)
);


-- public.owner_sources definition
CREATE TABLE public.owner_sources (
	owner_id varchar(255) NOT NULL,
	sources_id varchar(255) NOT NULL,
	CONSTRAINT owner_sources_pkey PRIMARY KEY (owner_id, sources_id),
	CONSTRAINT uk_ifriihpwqdiyfmcosywfae9s UNIQUE (sources_id),
	CONSTRAINT fk8l1oqfl2ljrcgpuj4wq03jic5 FOREIGN KEY (sources_id) REFERENCES public."source"(id),
	CONSTRAINT fks66rsepc2my0suf4thcdq2h2a FOREIGN KEY (owner_id) REFERENCES public."owner"(id)
);


-- public.partitioning definition
CREATE TABLE public.partitioning (
	id varchar(255) NOT NULL,
	closing_date timestamp NULL,
	"label" varchar(255) NULL,
	opening_date timestamp NULL,
	return_date timestamp NULL,
	campaign_id varchar(255) NULL,
	CONSTRAINT partitioning_pkey PRIMARY KEY (id),
	CONSTRAINT fkhh3jep6bbc2nu1fq624pruxb2 FOREIGN KEY (campaign_id) REFERENCES public.campaign(id)
);
CREATE INDEX campainid_index ON public.partitioning USING btree (campaign_id);


-- public.partitioning_params definition
CREATE TABLE public.partitioning_params (
	partitioning_id varchar(255) NOT NULL,
	params_metadata_id varchar(255) NOT NULL,
	params_param_id varchar(255) NOT NULL,
	CONSTRAINT partitioning_params_pkey PRIMARY KEY (partitioning_id, params_metadata_id, params_param_id),
	CONSTRAINT ukbmyqvee51p8o5j8c5mh41uwgn UNIQUE (params_metadata_id, params_param_id),
	CONSTRAINT fkdgcag5ysu7roq9kn5i710l3fy FOREIGN KEY (params_metadata_id,params_param_id) REFERENCES public.parameters(metadata_id,param_id),
	CONSTRAINT fksh97u6asr5rg4k9muqjpo16si FOREIGN KEY (partitioning_id) REFERENCES public.partitioning(id)
);


-- public.questioning definition
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


-- public.questioning_accreditation definition
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


-- public.questioning_communication definition
CREATE TABLE public.questioning_communication (
	id int8 NOT NULL,
	"date" timestamp(6) NULL,
	status varchar(255) NULL,
	"type" varchar(255) NULL,
	questioning_id int8 NULL,
	CONSTRAINT questioning_communication_pkey PRIMARY KEY (id),
	CONSTRAINT questioning_communication_status_check CHECK (((status)::text = ANY ((ARRAY['AUTOMATIC'::character varying, 'MANUAL'::character varying])::text[]))),
	CONSTRAINT questioning_communication_type_check CHECK (((type)::text = ANY ((ARRAY['COURRIER_OUVERTURE'::character varying, 'MAIL_OUVERTURE'::character varying, 'COURRIER_RELANCE'::character varying, 'MAIL_RELANCE'::character varying, 'COURRIER_MED'::character varying, 'COURRIER_CNR'::character varying])::text[]))),
	CONSTRAINT fkrs4r6iv2ckjlqy5xwt5026jqb FOREIGN KEY (questioning_id) REFERENCES public.questioning(id)
);
CREATE INDEX idquestioningcomm_index ON public.questioning_communication USING btree (questioning_id);


-- public.questioning_event definition
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


-- public.questioning_questioning_accreditations definition
CREATE TABLE public.questioning_questioning_accreditations (
	questioning_id int8 NOT NULL,
	questioning_accreditations_id int8 NOT NULL,
	CONSTRAINT questioning_questioning_accreditations_pkey PRIMARY KEY (questioning_id, questioning_accreditations_id),
	CONSTRAINT uk_ei2fadjuqk3e6cqc8yf052ayy UNIQUE (questioning_accreditations_id),
	CONSTRAINT fk6hm62jl0inwt1iy4gcc3yqtpj FOREIGN KEY (questioning_accreditations_id) REFERENCES public.questioning_accreditation(id),
	CONSTRAINT fkdwng4gagjr18rkqiv3pw02909 FOREIGN KEY (questioning_id) REFERENCES public.questioning(id)
);


-- public.questioning_questioning_comments definition
CREATE TABLE public.questioning_questioning_comments (
	questioning_id int8 NOT NULL,
	questioning_comments_id int8 NOT NULL,
	CONSTRAINT questioning_questioning_comments_pkey PRIMARY KEY (questioning_id, questioning_comments_id),
	CONSTRAINT uk7epo4xkvex2qu51untc3qw9b8 UNIQUE (questioning_comments_id),
	CONSTRAINT fk9f73oysodei1jqon0ensnkdyk FOREIGN KEY (questioning_id) REFERENCES public.questioning(id),
	CONSTRAINT fkhbqlgek19hdvjwrr1qtumrjxl FOREIGN KEY (questioning_comments_id) REFERENCES public.questioning_comment(id)
);


-- public.questioning_questioning_communications definition
CREATE TABLE public.questioning_questioning_communications (
	questioning_id int8 NOT NULL,
	questioning_communications_id int8 NOT NULL,
	CONSTRAINT questioning_questioning_communications_pkey PRIMARY KEY (questioning_id, questioning_communications_id),
	CONSTRAINT uk3qoakoe8rad97fbjxtxg27fd1 UNIQUE (questioning_communications_id),
	CONSTRAINT fk91hpf5fn9rj5slpcb72s72w9h FOREIGN KEY (questioning_communications_id) REFERENCES public.questioning_communication(id),
	CONSTRAINT fkin5f77vsq6j3h3x5u4hl3vh8t FOREIGN KEY (questioning_id) REFERENCES public.questioning(id)
);


-- public.questioning_questioning_events definition
CREATE TABLE public.questioning_questioning_events (
	questioning_id int8 NOT NULL,
	questioning_events_id int8 NOT NULL,
	CONSTRAINT questioning_questioning_events_pkey PRIMARY KEY (questioning_id, questioning_events_id),
	CONSTRAINT uk_le715nq8eddp9prnqewnvspgt UNIQUE (questioning_events_id),
	CONSTRAINT fkqqgm45333u11p2mx8lh9oyxee FOREIGN KEY (questioning_events_id) REFERENCES public.questioning_event(id),
	CONSTRAINT fkrgus17c52odepk751f4ha1xmi FOREIGN KEY (questioning_id) REFERENCES public.questioning(id)
);


-- public.source_surveys definition
CREATE TABLE public.source_surveys (
	source_id varchar(255) NOT NULL,
	surveys_id varchar(255) NOT NULL,
	CONSTRAINT source_surveys_pkey PRIMARY KEY (source_id, surveys_id),
	CONSTRAINT uk_ly3qns9058bmlxfxria4jlrt3 UNIQUE (surveys_id),
	CONSTRAINT fkabsmioq8y0ff0joaxuyok2jav FOREIGN KEY (source_id) REFERENCES public."source"(id),
	CONSTRAINT fkewypaxygju2po7g8jovv4pfh3 FOREIGN KEY (surveys_id) REFERENCES public.survey(id)
);


-- public.survey_campaigns definition
CREATE TABLE public.survey_campaigns (
	survey_id varchar(255) NOT NULL,
	campaigns_id varchar(255) NOT NULL,
	CONSTRAINT survey_campaigns_pkey PRIMARY KEY (survey_id, campaigns_id),
	CONSTRAINT uk_dg2v0pfxgejr7dcjmmjv4lgwq UNIQUE (campaigns_id),
	CONSTRAINT fkln5v5p62955ohj6ynjk5skysc FOREIGN KEY (survey_id) REFERENCES public.survey(id),
	CONSTRAINT fkrkd2l9sp1028penk6lms6rkxr FOREIGN KEY (campaigns_id) REFERENCES public.campaign(id)
);


-- public.survey_unit_questionings definition
CREATE TABLE public.survey_unit_questionings (
	survey_unit_id_su varchar(255) NOT NULL,
	questionings_id int8 NOT NULL,
	CONSTRAINT survey_unit_questionings_pkey PRIMARY KEY (survey_unit_id_su, questionings_id),
	CONSTRAINT uk_da4wcnr3w7kwjssypjskh5fri UNIQUE (questionings_id),
	CONSTRAINT fkh9ic8ny0dprapay1stvh428v0 FOREIGN KEY (questionings_id) REFERENCES public.questioning(id),
	CONSTRAINT fkm0hcq72i7sssmlydtsoc0rrdc FOREIGN KEY (survey_unit_id_su) REFERENCES public.survey_unit(id_su)
);


-- public.campaign_partitionings definition
CREATE TABLE public.campaign_partitionings (
	campaign_id varchar(255) NOT NULL,
	partitionings_id varchar(255) NOT NULL,
	CONSTRAINT campaign_partitionings_pkey PRIMARY KEY (campaign_id, partitionings_id),
	CONSTRAINT uk_trjbwn91w9d7r1l3bjbgkyle5 UNIQUE (partitionings_id),
	CONSTRAINT fkcrayhywc703pxqw6r9le1rvdi FOREIGN KEY (partitionings_id) REFERENCES public.partitioning(id),
	CONSTRAINT fke18nmg1aulyvxm0cbw5lyy156 FOREIGN KEY (campaign_id) REFERENCES public.campaign(id)
);

--changeset davdarras:001-02 context:init-db
INSERT INTO public.event_order (id,event_order,status) VALUES
	 (8,8,'REFUSAL'),
	 (7,7,'VALINT'),
	 (6,6,'VALPAP'),
	 (5,5,'HC'),
	 (4,4,'PARTIELINT'),
	 (3,3,'WASTE'),
	 (2,2,'PND'),
	 (1,1,'INITLA');

