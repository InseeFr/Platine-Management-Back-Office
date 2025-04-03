--changeset davdarras:demo-data context:demo

INSERT INTO public."owner"
(id, "label", ministry, logo)
VALUES('platine', 'owner Platine', 'SNDIL', '1');
INSERT INTO public."owner"
(id, "label", ministry, logo)
VALUES('agri', 'SSM Agriculture', NULL, NULL);

INSERT INTO public."support"
(id, "label", city, country_name, mail, phone_number, street_name, street_number, zip_code)
VALUES('agri-bsva', 'SSM Agriculture BSVA', 'Paris', 'France', 'support3@cocorico.fr', '0100000000', 'rue de Paris', '1', '75000');
INSERT INTO public."support"
(id, "label", city, country_name, mail, phone_number, street_name, street_number, zip_code)
VALUES('platine', 'Support platine', 'Paris', 'France', 'support3@cocorico.fr', '0100000000', 'rue de Paris', '1', '75000');

INSERT INTO public."source"
(id, long_wording, mandatory_my_surveys, periodicity, short_wording, owner_id, support_id)
VALUES('AQV', 'Source trest AQV', false, 'X', 'AQV stromae V2', 'platine', 'platine');

INSERT INTO public.survey
(id, cnis_url, communication, diffusion_url, long_objectives, long_wording, notice_url, sample_size, short_objectives, short_wording, specimen_url, visa_number, year_value, source_id)
VALUES('AQV2022', 'http://cnis/AQV2022', '', 'http://diffusion/AQV2022', 'Cette enquête permet de connaître précisément...', 'AQV stromae V2', 'http://notice/AQV2023', 0, 'Cette enquête permet de connaître précisément ...', 'Test pour AQV', 'http://specimenUrl/AQV2022', '2022xxxxxx', 2022, 'AQV');

INSERT INTO public.survey
(id, cnis_url, communication, diffusion_url, long_objectives, long_wording, notice_url, sample_size, short_objectives, short_wording, specimen_url, visa_number, year_value, source_id)
VALUES('AQV2023', 'http://cnis/AQV2023', '', 'http://diffusion/AQV2023', 'Cette enquête permet de connaître précisément...', 'AQV stromae V2', 'http://notice/AQV2023', 0, 'Cette enquête permet de connaître précisément ...', 'Test pour AQV', 'http://specimenUrl/AQV2022', '2023xxxxxx', 2023, 'AQV');

INSERT INTO public.survey
(id, cnis_url, communication, diffusion_url, long_objectives, long_wording, notice_url, sample_size, short_objectives, short_wording, specimen_url, visa_number, year_value, source_id)
VALUES('AQV2024', 'http://cnis/AQV2024', '', 'http://diffusion/AQV2024', 'Cette enquête permet de connaître précisément...', 'AQV stromae V2', 'http://notice/AQV2023', 0, 'Cette enquête permet de connaître précisément ...', 'Test pour AQV', 'http://specimenUrl/AQV2022', '2024xxxxxx', 2024, 'AQV');


INSERT INTO public.campaign
(id, campaign_wording, survey_id, period_value, year_value, datacollection_target, sensitivity)
VALUES('AQV2022X00', 'AQV2022X00', 'AQV2022', 'X00', 2022, 'LUNATIC_NORMAL', false);
INSERT INTO public.campaign
(id, campaign_wording, survey_id, period_value, year_value, datacollection_target, sensitivity)
VALUES('AQV2024X00', 'AQV2024X00', 'AQV2024', 'X00', 2024, 'LUNATIC_NORMAL', false);
INSERT INTO public.campaign
(id, campaign_wording, survey_id, period_value, year_value, datacollection_target, sensitivity)
VALUES('AQV2023X00', 'Campagne qualité volaille en 2023 - AQV2023X00', 'AQV2023', 'X00', 2023, 'LUNATIC_SENSITIVE', true);


INSERT INTO public.partitioning
(id, closing_date, opening_date, return_date, campaign_id, "label")
VALUES('AQV2024X0000', '2024-12-30 01:00:00.000', '2023-03-07 01:00:00.000', '2024-12-31 10:33:27.723',  'AQV2024X00', 'vague 00');
INSERT INTO public.partitioning
(id, closing_date, opening_date, return_date, campaign_id, "label")
VALUES('AQV2023X0000', '2024-11-30 01:00:00.000', '2022-04-20 18:27:02.745', '2024-12-26 07:28:27.086', 'AQV2023X00', NULL);
INSERT INTO public.partitioning
(id, closing_date, opening_date, return_date, campaign_id, "label")
VALUES('AQV2022X0000', '2024-06-01 02:00:00.000', '2022-03-08 10:33:27.723', '2022-12-31 10:33:27.723', 'AQV2022X00', 'vague 00');















INSERT INTO public.address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(207, 'France', 'rue des oies', '1', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(211, 'France', 'rue des oies', '1', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(214, 'France', 'rue des oies', '3', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(217, 'France', 'rue des oies', '4', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(1209, 'Italie', NULL, '', '', 'ComplementAdresse0', '', 'LibelleCedex0', 'LibelleCommune0', NULL, 'IndiceRepetition0', 'MentionSpeciale0', 'TypeVoie0');



INSERT INTO public.contact
(identifier, "comment", email, first_name, "function", gender, last_name, phone, address_id, external_id, email_verify,  usual_company_name, phone2)
VALUES('RESPON1', NULL, 'caille1@insee.fr', 'Éléonore ', 'Directrice', 'Female', 'Iléosud', '0600000000', 207, 'ID1', false, NULL, NULL);
INSERT INTO public.contact
(identifier, "comment", email, first_name, "function", gender, last_name, phone, address_id, external_id, email_verify,  usual_company_name, phone2)
VALUES('RESPON2', NULL, 'caille2@insee.fr', 'Éléonore ', 'Directrice', 'Female', 'Iléosud', '0600000000', 211, 'ID1', false,  'Cabinet comptable', NULL);
INSERT INTO public.contact
(identifier, "comment", email, first_name, "function", gender, last_name, phone, address_id, external_id, email_verify,  usual_company_name, phone2)
VALUES('RESPON3', NULL, 'caille3@insee.fr', 'Ève ', 'Directrice', 'Female', 'Heille Des Sens', '0600000000', 214, NULL, false,  NULL, NULL);
INSERT INTO public.contact
(identifier, "comment", email, first_name, "function", gender, last_name, phone, address_id, external_id, email_verify,  usual_company_name, phone2)
VALUES('RESPON4', NULL, 'caille4@insee.fr', 'Jean  ', 'Directeur', 'Male', 'Némard', '0600000000', 217, NULL, false,  NULL, NULL);
INSERT INTO public.contact
(identifier, "comment", email, first_name, "function", gender, last_name, phone, address_id, external_id, email_verify,  usual_company_name, phone2)
VALUES('RESPON5', 'Commentaire0', 'caille5@insee.fr', '', 'Comptable', 'Undefined', '', '', 1209, NULL, false,  NULL, NULL);

INSERT INTO public.internal_users (identifier, role) VALUES('GESTIO1', 2);
INSERT INTO public.internal_users (identifier, role) VALUES('GESTIO2', 2);

INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(215, '2022-11-18 15:45:35.901', 0, 'RESPON3', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(218, '2022-11-18 15:45:48.459', 0, 'RESPON4', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(308, '2022-12-02 10:07:41.560', 1, 'RESPON3', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(309, '2022-12-02 12:01:39.516', 1, 'RESPON3', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(973, '2023-02-24 13:06:18.739', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(991, '2023-02-24 16:21:34.584', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1046, '2023-02-28 14:10:49.996', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(212, '2022-11-18 15:45:05.761', 0, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(310, '2022-12-05 14:22:54.760', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1180, '2023-03-06 21:10:31.513', 3, 'RESPON2', '{"source": "MySurveys IHM"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1181, '2023-03-07 08:06:40.944', 3, 'RESPON1', '{"source": "MySurveys IHM"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1182, '2023-03-07 08:07:04.751', 3, 'RESPON3', '{"source": "MySurveys IHM"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1210, '2023-03-08 12:13:58.828', 0, 'RESPON5', '{"source": "platine-batch"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1214, '2023-03-08 12:17:21.151', 3, 'RESPON5', '{"source": "MySurveys IHM"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1218, '2023-03-08 15:32:36.969', 3, 'RESPON4', '{"source": "MySurveys IHM"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(552, '2024-01-09 16:49:26.696', 1, 'RESPON2', '{"author": "caille2"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(553, '2024-01-09 16:50:58.696', 1, 'RESPON2', '{"author": "caille2"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(752, '2024-01-25 15:06:03.186', 1, 'RESPON1', '{"author": "y72wvh"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(208, '2022-11-18 15:40:21.978', 0, 'RESPON1', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(210, '2022-11-18 15:40:29.337', 1, 'RESPON1', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(753, '2024-01-25 15:07:28.844', 1, 'RESPON2', '{"author": "y72wvh"}'::jsonb);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(376, '2023-01-13 10:26:31.223', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(377, '2023-01-13 10:26:51.221', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(381, '2023-01-18 14:57:44.736', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(974, '2023-02-24 13:06:47.613', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(975, '2023-02-24 13:07:08.768', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(976, '2023-02-24 13:10:35.058', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(977, '2023-02-24 13:10:56.955', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(979, '2023-02-24 13:41:33.706', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(980, '2023-02-24 13:41:33.646', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(981, '2023-02-24 13:45:38.478', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(982, '2023-02-24 13:45:38.402', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(984, '2023-02-24 14:32:47.677', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(985, '2023-02-24 14:32:47.578', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1179, '2023-03-06 20:05:38.379', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1408, '2023-03-21 16:19:42.648', 1, 'RESPON2', NULL);
INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES(1409, '2023-03-21 16:19:45.648', 1, 'RESPON2', NULL);




INSERT INTO public.survey_unit_address
(id, country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(222, 'France', 'allée perdue', '3', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(223, 'France', 'allée perdue', '4', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(224, 'France', 'allée perdue', '5', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id, country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(225, 'France', 'allée perdue', '6', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(226,  'France', 'allée perdue', '7', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(227, 'France', 'allée perdue', '8', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(228, 'France', 'allée perdue', '9', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(229, 'France', 'allée perdue', '10', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(240, 'France', 'allée perdue', '11', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(244,  'France', 'allée perdue', '14', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(245,  'France', 'allée perdue', '13', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id, country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(246, 'France', 'allée perdue', '15', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(247,'France', 'allée perdue', '16', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id, country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(248, 'Italie', 'allée perdue', '17', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(249, 'France', 'allée perdue', '18', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(250,  'France', 'allée perdue', '19', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);
INSERT INTO public.survey_unit_address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES(251,  'France', 'allée perdue', '20', '84562', NULL, NULL, NULL, 'Very-long-name-for-a-city', NULL, NULL, NULL, NULL);



INSERT INTO public.survey_unit
(id_su, identification_name, identification_code,  "label")
VALUES('PROTO01', 'Survey unit 1', 'PROTO01',  'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code,  "label")
VALUES('PROTO02', 'Survey unit 2', 'PROTO02',  'association');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO03', 'Survey unit 3', 'PROTO03', 222, 'établissement');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO04', 'Survey unit 4', 'PROTO04', 223, 'établissement');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO05', 'Survey unit 5', 'PROTO05', 224, 'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO06', 'Survey unit 6', 'PROTO06', 225, 'association');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO07', 'Survey unit 7', 'PROTO07', 226, 'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO08', 'Survey unit 8', 'PROTO08', 227, 'établissement');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO09', 'Survey unit 9', 'PROTO09', 228, 'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO10', 'Survey unit 10', 'PROTO10', 229, 'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO11', 'Survey unit 11', 'PROTO11', 240, 'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO12', 'Survey unit 12', 'PROTO12', null, 'association');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO13', 'Survey unit 13', 'PROTO13', 245, 'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO14', 'Survey unit 14', 'PROTO14', 244, 'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO15', 'Survey unit 15', 'PROTO15', 246, 'établissement');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO16', 'Survey unit 16', 'PROTO16', 247, 'association');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO17', 'Survey unit 17', 'PROTO17', 248, 'établissement');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO18', 'Survey unit 18', 'PROTO18', 249, 'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO19', 'Survey unit 19', 'PROTO19', 250, 'établissement');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO20', 'Survey unit 20', 'PROTO20', 251, 'entreprise');
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO21', 'raison sociale 001', 'SIREN001', NULL, NULL);
INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES('PROTO99', 'raison sociale 001', 'SIREN001', NULL, NULL);





INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(230, 'AQV2023X0000', 'aqv2023x00', 'PROTO01');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(231, 'AQV2023X0000', 'aqv2023x00', 'PROTO02');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(232, 'AQV2023X0000', 'aqv2023x00', 'PROTO03');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(233, 'AQV2023X0000', 'aqv2023x00', 'PROTO04');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(234, 'AQV2023X0000', 'aqv2023x00', 'PROTO05');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(235, 'AQV2023X0000', 'aqv2023x00', 'PROTO06');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(236, 'AQV2023X0000', 'aqv2023x00', 'PROTO07');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(237, 'AQV2023X0000', 'aqv2023x00', 'PROTO08');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(238, 'AQV2023X0000', 'aqv2023x00', 'PROTO09');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(239, 'AQV2023X0000', 'aqv2023x00', 'PROTO10');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(252, 'AQV2023X0000', 'aqv2023x00', 'PROTO11');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(253, 'AQV2023X0000', 'aqv2023x00', 'PROTO12');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(254, 'AQV2023X0000', 'aqv2023x00', 'PROTO13');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(255, 'AQV2023X0000', 'aqv2023x00', 'PROTO14');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(256, 'AQV2023X0000', 'aqv2023x00', 'PROTO15');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(257, 'AQV2023X0000', 'aqv2023x00', 'PROTO16');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(258, 'AQV2023X0000', 'aqv2023x00', 'PROTO17');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(259, 'AQV2023X0000', 'aqv2023x00', 'PROTO18');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(260, 'AQV2023X0000', 'aqv2023x00', 'PROTO19');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(261, 'AQV2023X0000', 'aqv2023x00', 'PROTO20');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(1208, 'AQV2022X0000', 'aqv2022x00', 'PROTO21');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(1215, 'AQV2023X0000', 'aqv2023x00', 'PROTO21');
INSERT INTO public.questioning
(id, id_partitioning, model_name, survey_unit_id_su)
VALUES(1219, 'AQV2024X0000', 'aqv2024x00', 'PROTO21');





INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(262, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 230);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(264, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 231);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(266, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 232);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(268, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 233);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(270, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 234);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(272, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 235);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(274, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 236);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(276, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 237);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(278, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 238);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(280, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, 239);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(282, 'string', '2022-11-18 16:12:49.697', 'RESPON3', false, 239);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(284, 'string', '2022-11-18 16:12:49.697', 'RESPON3', false, 238);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(286, 'string', '2022-11-18 16:12:49.697', 'RESPON3', false, 237);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(288, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true, 252);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(290, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true, 253);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(292, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true, 254);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(294, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true, 255);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(296, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true, 256);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(298, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true, 257);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(300, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true, 258);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(302, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true, 259);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(304, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true, 260);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(306, 'string', '2022-11-18 16:12:49.697', 'RESPON4', true, 261);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(1212, 'platine-batch', '2023-03-08 12:13:58.973', 'RESPON5', true, 1208);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(1216, 'platine-batch', '2023-03-08 12:25:33.484', 'RESPON5', true, 1215);
INSERT INTO public.questioning_accreditation
(id, creation_author, creation_date, id_contact, is_main, questioning_id)
VALUES(1220, 'platine-batch', '2023-03-08 15:49:51.670', 'RESPON5', true, 1219);


INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(398, '2023-02-03 16:32:16.116', 'VALINT', 230, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(331, '2023-01-11 11:05:07.380', 'INITLA', 230, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(406, '2023-02-10 11:04:43.737', 'VALINT', 231, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(397, '2023-02-01 11:09:48.967', 'PARTIELINT', 231, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(332, '2023-01-11 11:05:07.380', 'INITLA', 231, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(1410, '2023-03-24 12:06:38.494', 'VALINT', 232, '{"source": "platine-batch"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(333, '2023-01-11 11:05:07.380', 'INITLA', 232, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(334, '2023-01-11 11:05:07.380', 'INITLA', 233, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(335, '2023-01-11 11:05:07.380', 'INITLA', 234, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(336, '2023-01-11 11:05:07.380', 'INITLA', 235, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(1411, '2023-03-24 12:06:38.503', 'PARTIELINT', 236, '{"source": "platine-batch"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(337, '2023-01-11 11:05:07.380', 'INITLA', 236, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(338, '2023-01-11 11:05:07.380', 'INITLA', 237, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(339, '2023-01-11 11:05:07.380', 'INITLA', 238, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(2620506, '2025-01-22 13:22:46.225', 'REFUSAL', 238, '{"source": "platine-gestion"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(1412, '2023-03-24 12:06:38.508', 'PARTIELINT', 239, '{"source": "platine-batch"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(341, '2023-01-11 11:05:07.380', 'INITLA', 239, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(2596506, '2024-03-05 15:25:59.748', 'HC', 239, '{"source": "platine-gestion"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(2596556, '2024-03-05 15:25:59.748', 'HC', 239, '{"source": "platine-gestion"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(342, '2023-01-11 11:05:07.380', 'INITLA', 252, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(343, '2023-01-11 11:05:07.380', 'INITLA', 253, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(344, '2023-01-11 11:05:07.380', 'INITLA', 254, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(345, '2023-01-11 11:05:07.380', 'INITLA', 255, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(346, '2023-01-11 11:05:07.380', 'INITLA', 256, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(347, '2023-01-11 11:05:07.380', 'INITLA', 257, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(348, '2023-01-11 11:05:07.380', 'INITLA', 258, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(349, '2023-01-11 11:05:07.380', 'INITLA', 259, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(350, '2023-01-11 11:05:07.380', 'INITLA', 260, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(351, '2023-01-11 11:05:07.380', 'INITLA', 261, '{}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(2820, '2023-06-01 13:53:50.656', 'VALINT', 261, '{"source": "platine-batch"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(1213, '2023-03-08 12:13:59.566', 'INITLA', 1208, '{"source": "platine-batch"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(1217, '2023-03-08 12:25:33.960', 'INITLA', 1215, '{"source": "platine-batch"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(1221, '2023-03-08 15:49:52.310', 'INITLA', 1219, '{"source": "platine-batch"}'::jsonb, NULL);
INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES(2822, '2023-06-01 13:53:50.683', 'PARTIELINT', 1219, '{"source": "platine-batch"}'::jsonb, NULL);


INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(209, NULL, NULL, 'RESPON1');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(263, 'AQV2023X00', 'PROTO01', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(265, 'AQV2023X00', 'PROTO02', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(267, 'AQV2023X00', 'PROTO03', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(269, 'AQV2023X00', 'PROTO04', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(271, 'AQV2023X00', 'PROTO05', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(273, 'AQV2023X00', 'PROTO06', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(275, 'AQV2023X00', 'PROTO07', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(277, 'AQV2023X00', 'PROTO08', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(279, 'AQV2023X00', 'PROTO09', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(281, 'AQV2023X00', 'PROTO10', 'RESPON2');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(283, 'AQV2023X00', 'PROTO10', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(285, 'AQV2023X00', 'PROTO09', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(287, 'AQV2023X00', 'PROTO08', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(289, 'AQV2023X00', 'PROTO11', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(291, 'AQV2023X00', 'PROTO12', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(293, 'AQV2023X00', 'PROTO13', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(295, 'AQV2023X00', 'PROTO14', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(297, 'AQV2023X00', 'PROTO15', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(299, 'AQV2023X00', 'PROTO16', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(301, 'AQV2023X00', 'PROTO17', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(303, 'AQV2023X00', 'PROTO18', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(305, 'AQV2023X00', 'PROTO19', 'RESPON3');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(307, 'AQV2023X00', 'PROTO20', 'RESPON4');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(1211, 'AQV2022X00', 'PROTO21', 'RESPON5');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(2000, 'AQV2023X00', 'PROTO21', 'RESPON5');
INSERT INTO public."view"
(id, campaign_id, id_su, identifier)
VALUES(2001, 'AQV2024X00', 'PROTO21', 'RESPON5');

INSERT INTO public.questioning_comment
(id, author, "comment", "date", questioning_id)
VALUES(252, 'Robert', 'Commentaire 1 PROTO001', '2025-01-24 14:06:44.706', 1215);
INSERT INTO public.questioning_comment
(id, author, "comment", "date", questioning_id)
VALUES(253, 'Jacqueline', 'Commentaire 2 PROTO002', '2025-01-24 14:06:55.439', 1215);
INSERT INTO public.questioning_comment
(id, author, "comment", "date", questioning_id)
VALUES(254, 'Nicolas', 'Commentaire Interrogation 1208', '2025-01-24 14:07:37.855', 1208);

INSERT INTO public.survey_unit_comment
(id, author, "comment", "date", survey_unit_id_su)
VALUES(652, 'David', 'Commentaire UE PROTO001', '2025-01-24 14:07:07.945', 'PROTO21');


select distinct(qa.questioning_id)  from questioning_accreditation qa where qa.id_contact like 'CAILL%' order by qa.questioning_id;


INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10000, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 233);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10001, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 234);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10002, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 235);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10003, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 236);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10004, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 237);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10005, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 238);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10006, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 239);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10007, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 252);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10008, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 253);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10009, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 254);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10010, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 255);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10011, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 256);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10012, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 257);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10013, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 258);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10014, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 259);
INSERT INTO public.questioning_communication
(id, "date", status, "type", questioning_id)
VALUES(10015, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE', 260);

INSERT INTO public.contact_source (source_id, survey_unit_id, contact_id, is_main)
SELECT source_id, survey_unit_id_su, id_contact, is_main
FROM (
    SELECT
        s.source_id,
        q.survey_unit_id_su,
        qa.id_contact,
        qa.is_main,
        ROW_NUMBER() OVER (
            PARTITION BY s.source_id, q.survey_unit_id_su, qa.id_contact
            ORDER BY CASE WHEN qa.is_main THEN 1 ELSE 2 END
        ) AS rn
    FROM public.questioning q
    JOIN public.questioning_accreditation qa ON q.id = qa.questioning_id
    JOIN public.partitioning p ON q.id_partitioning = p.id
    JOIN public.campaign c ON p.campaign_id = c.id
    JOIN public.survey s ON c.survey_id = s.id
) sub
WHERE rn = 1;

