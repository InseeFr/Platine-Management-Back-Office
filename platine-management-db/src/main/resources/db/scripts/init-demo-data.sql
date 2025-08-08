--changeset davdarras:demo-data context:demo

INSERT INTO public."owner"(id, "label", ministry, logo) VALUES
  ('platine', 'owner Platine', 'SNDIL', '1'),
  ('agri', 'SSM Agriculture', NULL, NULL);

INSERT INTO public."support"(id, "label", city, country_name, mail, phone_number, street_name, street_number, zip_code) VALUES
  ('agri-bsva', 'SSM Agriculture BSVA', 'Paris', 'France', 'support3@cocorico.fr', '0100000000', 'rue de Paris', '1', '75000'),
  ('platine', 'Support platine', 'Paris', 'France', 'support3@cocorico.fr', '0100000000', 'rue de Paris', '1', '75000');

INSERT INTO public."source"(id, long_wording, mandatory_my_surveys, periodicity, short_wording, owner_id, support_id) VALUES
  ('AQV', 'Source trest AQV', false, 'X', 'AQV stromae V2', 'platine', 'platine');

INSERT INTO public.survey(id, cnis_url, communication, diffusion_url, long_objectives, long_wording, notice_url, sample_size, short_objectives, short_wording, specimen_url, visa_number, year_value, source_id) VALUES
  ('AQV2022', 'http://cnis/AQV2022', '', 'http://diffusion/AQV2022', 'Cette enquête permet de connaître précisément...', 'AQV stromae V2', 'http://notice/AQV2023', 0, 'Cette enquête permet de connaître précisément ...', 'Test pour AQV', 'http://specimenUrl/AQV2022', '2022xxxxxx', 2022, 'AQV'),
  ('AQV2023', 'http://cnis/AQV2023', '', 'http://diffusion/AQV2023', 'Cette enquête permet de connaître précisément...', 'AQV stromae V2', 'http://notice/AQV2023', 0, 'Cette enquête permet de connaître précisément ...', 'Test pour AQV', 'http://specimenUrl/AQV2022', '2023xxxxxx', 2023, 'AQV'),
  ('AQV2024', 'http://cnis/AQV2024', '', 'http://diffusion/AQV2024', 'Cette enquête permet de connaître précisément...', 'AQV stromae V2', 'http://notice/AQV2023', 0, 'Cette enquête permet de connaître précisément ...', 'Test pour AQV', 'http://specimenUrl/AQV2022', '2024xxxxxx', 2024, 'AQV');

INSERT INTO public.campaign(id, campaign_wording, survey_id, period_value, year_value, datacollection_target, sensitivity) VALUES
  ('AQV2022X00', 'AQV2022X00', 'AQV2022', 'X00', 2022, 'LUNATIC_NORMAL', false),
  ('AQV2024X00', 'AQV2024X00', 'AQV2024', 'X00', 2024, 'LUNATIC_NORMAL', false),
  ('AQV2023X00', 'Campagne qualité volaille en 2023 - AQV2023X00', 'AQV2023', 'X00', 2023, 'LUNATIC_SENSITIVE', true);

INSERT INTO public.partitioning (id, closing_date, opening_date, return_date, campaign_id, "label") VALUES
  ('AQV2024X0000', '2099-12-30 01:00:00.000', '2023-03-07 01:00:00.000', '2024-12-31 10:33:27.723',  'AQV2024X00', 'vague 00'),
  ('AQV2023X0000', '2099-11-30 01:00:00.000', '2022-04-20 18:27:02.745', '2024-12-26 07:28:27.086', 'AQV2023X00', NULL),
  ('AQV2022X0000', '2099-10-30 02:00:00.000', '2022-03-08 10:33:27.723', '2022-12-31 10:33:27.723', 'AQV2022X00', 'vague 00');

INSERT INTO public.address(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type) VALUES
  (207, 'France', 'rue des oies', '1', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (211, 'France', 'rue des oies', '1', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (214, 'France', 'rue des oies', '3', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (217, 'France', 'rue des oies', '4', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (1209, 'Italie', NULL, '', '', 'ComplementAdresse0', '', 'LibelleCedex0', 'LibelleCommune0', NULL, 'IndiceRepetition0', 'MentionSpeciale0', 'TypeVoie0');

INSERT INTO public.contact(identifier, "comment", email, first_name, "function", gender, last_name, phone, address_id, external_id, email_verify,  usual_company_name, phone2) VALUES
  ('RESPON1', NULL, 'caille1@insee.fr', 'Éléonore ', 'Directrice', 'Female', 'Iléosud', '0600000000', 207, 'ID1', false, NULL, NULL),
  ('RESPON2', NULL, 'caille2@insee.fr', 'Éléonore ', 'Directrice', 'Female', 'Iléosud', '0600000000', 211, 'ID1', false,  'Cabinet comptable', NULL),
  ('RESPON3', NULL, 'caille3@insee.fr', 'Ève ', 'Directrice', 'Female', 'Heille Des Sens', '0600000000', 214, NULL, false,  NULL, NULL),
  ('RESPON4', NULL, 'caille4@insee.fr', 'Jean  ', 'Directeur', 'Male', 'Némard', '0600000000', 217, NULL, false,  NULL, NULL),
  ('RESPON5', 'Commentaire0', 'caille5@insee.fr', '', 'Comptable', 'Undefined', '', '', 1209, NULL, false,  NULL, NULL);

INSERT INTO public.internal_users (identifier, role) VALUES
  ('GESTIO1', 2),
  ('GESTIO2', 2);

INSERT INTO public.contact_event(id, event_date, "type", contact_identifier, payload) VALUES
  (215, '2022-11-18 15:45:35.901', 0, 'RESPON3', NULL),
  (218, '2022-11-18 15:45:48.459', 0, 'RESPON4', NULL),
  (308, '2022-12-02 10:07:41.560', 1, 'RESPON3', NULL),
  (309, '2022-12-02 12:01:39.516', 1, 'RESPON3', NULL),
  (973, '2023-02-24 13:06:18.739', 1, 'RESPON2', NULL),
  (991, '2023-02-24 16:21:34.584', 1, 'RESPON2', NULL),
  (1046, '2023-02-28 14:10:49.996', 1, 'RESPON2', NULL),
  (212, '2022-11-18 15:45:05.761', 0, 'RESPON2', NULL),
  (310, '2022-12-05 14:22:54.760', 1, 'RESPON2', NULL),
  (1180, '2023-03-06 21:10:31.513', 3, 'RESPON2', '{"source": "MySurveys IHM"}'::jsonb),
  (1181, '2023-03-07 08:06:40.944', 3, 'RESPON1', '{"source": "MySurveys IHM"}'::jsonb),
  (1182, '2023-03-07 08:07:04.751', 3, 'RESPON3', '{"source": "MySurveys IHM"}'::jsonb),
  (1210, '2023-03-08 12:13:58.828', 0, 'RESPON5', '{"source": "platine-batch"}'::jsonb),
  (1214, '2023-03-08 12:17:21.151', 3, 'RESPON5', '{"source": "MySurveys IHM"}'::jsonb),
  (1218, '2023-03-08 15:32:36.969', 3, 'RESPON4', '{"source": "MySurveys IHM"}'::jsonb),
  (552, '2024-01-09 16:49:26.696', 1, 'RESPON2', '{"author": "caille2"}'::jsonb),
  (553, '2024-01-09 16:50:58.696', 1, 'RESPON2', '{"author": "caille2"}'::jsonb),
  (752, '2024-01-25 15:06:03.186', 1, 'RESPON1', '{"author": "y72wvh"}'::jsonb),
  (208, '2022-11-18 15:40:21.978', 0, 'RESPON1', NULL),
  (210, '2022-11-18 15:40:29.337', 1, 'RESPON1', NULL),
  (753, '2024-01-25 15:07:28.844', 1, 'RESPON2', '{"author": "y72wvh"}'::jsonb),
  (376, '2023-01-13 10:26:31.223', 1, 'RESPON2', NULL),
  (377, '2023-01-13 10:26:51.221', 1, 'RESPON2', NULL),
  (381, '2023-01-18 14:57:44.736', 1, 'RESPON2', NULL),
  (974, '2023-02-24 13:06:47.613', 1, 'RESPON2', NULL),
  (975, '2023-02-24 13:07:08.768', 1, 'RESPON2', NULL),
  (976, '2023-02-24 13:10:35.058', 1, 'RESPON2', NULL),
  (977, '2023-02-24 13:10:56.955', 1, 'RESPON2', NULL),
  (979, '2023-02-24 13:41:33.706', 1, 'RESPON2', NULL),
  (980, '2023-02-24 13:41:33.646', 1, 'RESPON2', NULL),
  (981, '2023-02-24 13:45:38.478', 1, 'RESPON2', NULL),
  (982, '2023-02-24 13:45:38.402', 1, 'RESPON2', NULL),
  (984, '2023-02-24 14:32:47.677', 1, 'RESPON2', NULL),
  (985, '2023-02-24 14:32:47.578', 1, 'RESPON2', NULL),
  (1179, '2023-03-06 20:05:38.379', 1, 'RESPON2', NULL),
  (1408, '2023-03-21 16:19:42.648', 1, 'RESPON2', NULL),
  (1409, '2023-03-21 16:19:45.648', 1, 'RESPON2', NULL);

INSERT INTO public.survey_unit_address(id, country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type) VALUES
  (222, 'France', 'allée perdue', '3', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (223, 'France', 'allée perdue', '4', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (224, 'France', 'allée perdue', '5', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (225, 'France', 'allée perdue', '6', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (226,  'France', 'allée perdue', '7', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (227, 'France', 'allée perdue', '8', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (228, 'France', 'allée perdue', '9', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (229, 'France', 'allée perdue', '10', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (240, 'France', 'allée perdue', '11', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (244,  'France', 'allée perdue', '14', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (245,  'France', 'allée perdue', '13', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (246, 'France', 'allée perdue', '15', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (247,'France', 'allée perdue', '16', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (248, 'Italie', 'allée perdue', '17', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (249, 'France', 'allée perdue', '18', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (250,  'France', 'allée perdue', '19', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL),
  (251,  'France', 'allée perdue', '20', '84562', NULL, NULL, NULL, 'Very-long-name-for-a-city', NULL, NULL, NULL, NULL);

INSERT INTO public.survey_unit (id_su, identification_name, identification_code, survey_unit_address_id, "label") VALUES
  ('PROTO01', 'Survey unit 1', 'PROTO01', NULL, 'entreprise'),
  ('PROTO02', 'Survey unit 2', 'PROTO02', NULL, 'association'),
  ('PROTO03', 'Survey unit 3', 'PROTO03', 222, 'établissement'),
  ('PROTO04', 'Survey unit 4', 'PROTO04', 223, 'établissement'),
  ('PROTO05', 'Survey unit 5', 'PROTO05', 224, 'entreprise'),
  ('PROTO06', 'Survey unit 6', 'PROTO06', 225, 'association'),
  ('PROTO07', 'Survey unit 7', 'PROTO07', 226, 'entreprise'),
  ('PROTO08', 'Survey unit 8', 'PROTO08', 227, 'établissement'),
  ('PROTO09', 'Survey unit 9', 'PROTO09', 228, 'entreprise'),
  ('PROTO10', 'Survey unit 10', 'PROTO10', 229, 'entreprise'),
  ('PROTO11', 'Survey unit 11', 'PROTO11', 240, 'entreprise'),
  ('PROTO12', 'Survey unit 12', 'PROTO12', null, 'association'),
  ('PROTO13', 'Survey unit 13', 'PROTO13', 245, 'entreprise'),
  ('PROTO14', 'Survey unit 14', 'PROTO14', 244, 'entreprise'),
  ('PROTO15', 'Survey unit 15', 'PROTO15', 246, 'établissement'),
  ('PROTO16', 'Survey unit 16', 'PROTO16', 247, 'association'),
  ('PROTO17', 'Survey unit 17', 'PROTO17', 248, 'établissement'),
  ('PROTO18', 'Survey unit 18', 'PROTO18', 249, 'entreprise'),
  ('PROTO19', 'Survey unit 19', 'PROTO19', 250, 'établissement'),
  ('PROTO20', 'Survey unit 20', 'PROTO20', 251, 'entreprise'),
  ('PROTO21', 'raison sociale 001', 'SIREN001', NULL, NULL),
  ('PROTO99', 'raison sociale 001', 'SIREN001', NULL, NULL);

INSERT INTO public.questioning (id, id_partitioning, model_name, survey_unit_id_su) VALUES
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd01', 'AQV2023X0000', 'aqv2023x00', 'PROTO01'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd02', 'AQV2023X0000', 'aqv2023x00', 'PROTO02'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd03', 'AQV2023X0000', 'aqv2023x00', 'PROTO03'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd04', 'AQV2023X0000', 'aqv2023x00', 'PROTO04'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd05', 'AQV2023X0000', 'aqv2023x00', 'PROTO05'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd06', 'AQV2023X0000', 'aqv2023x00', 'PROTO06'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd07', 'AQV2023X0000', 'aqv2023x00', 'PROTO07'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd08', 'AQV2023X0000', 'aqv2023x00', 'PROTO08'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd09', 'AQV2023X0000', 'aqv2023x00', 'PROTO09'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd10', 'AQV2023X0000', 'aqv2023x00', 'PROTO10'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd11', 'AQV2023X0000', 'aqv2023x00', 'PROTO11'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd12', 'AQV2023X0000', 'aqv2023x00', 'PROTO12'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd13', 'AQV2023X0000', 'aqv2023x00', 'PROTO13'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd14', 'AQV2023X0000', 'aqv2023x00', 'PROTO14'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd15', 'AQV2023X0000', 'aqv2023x00', 'PROTO15'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd16', 'AQV2023X0000', 'aqv2023x00', 'PROTO16'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd17', 'AQV2023X0000', 'aqv2023x00', 'PROTO17'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd18', 'AQV2023X0000', 'aqv2023x00', 'PROTO18'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd19', 'AQV2023X0000', 'aqv2023x00', 'PROTO19'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd20', 'AQV2023X0000', 'aqv2023x00', 'PROTO20'),
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa21', 'AQV2022X0000', 'aqv2022x00', 'PROTO21'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb21', 'AQV2023X0000', 'aqv2023x00', 'PROTO21'),
  ('0c83fb82-0197-7197-8e8c-a6ce2c2dbd21', 'AQV2024X0000', 'aqv2024x00', 'PROTO21');

INSERT INTO public.questioning_accreditation (id, creation_author, creation_date, id_contact, is_main, questioning_id) VALUES
  (262, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd01'),
  (264, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd02'),
  (266, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd03'),
  (268, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd04'),
  (270, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd05'),
  (272, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd06'),
  (274, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd07'),
  (276, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd08'),
  (278, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd09'),
  (280, 'string', '2022-11-18 16:12:49.697', 'RESPON2', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd10'),
  (282, 'string', '2022-11-18 16:12:49.697', 'RESPON3', false, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd10'),
  (284, 'string', '2022-11-18 16:12:49.697', 'RESPON3', false, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd09'),
  (286, 'string', '2022-11-18 16:12:49.697', 'RESPON3', false, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd08'),
  (288, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd11'),
  (290, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd12'),
  (292, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd13'),
  (294, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd14'),
  (296, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd15'),
  (298, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd16'),
  (300, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd17'),
  (302, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd18'),
  (304, 'string', '2022-11-18 16:12:49.697', 'RESPON3', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd19'),
  (306, 'string', '2022-11-18 16:12:49.697', 'RESPON4', true,  '0c83fb82-0197-7197-8e8c-a6ce2c2dbd20'),
  (1212, 'platine-batch', '2023-03-08 12:13:58.973', 'RESPON5', true, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa21'),
  (1216, 'platine-batch', '2023-03-08 12:25:33.484', 'RESPON5', true, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb21'),
  (1220, 'platine-batch', '2023-03-08 15:49:51.670', 'RESPON5', true, '0c83fb82-0197-7197-8e8c-a6ce2c2dbd21');

INSERT INTO public.questioning_event (id, "date", "type", questioning_id, payload, id_upload) VALUES
  (398,  '2023-02-03 16:32:16.116', 'VALINT',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd01', '{}'::jsonb, NULL),
  (331,  '2023-01-11 11:05:07.380', 'INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd01', '{}'::jsonb, NULL),
  (406,  '2023-02-10 11:04:43.737', 'VALINT',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd02', '{}'::jsonb, NULL),
  (397,  '2023-02-01 11:09:48.967', 'PARTIELINT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd02', '{}'::jsonb, NULL),
  (332,  '2023-01-11 11:05:07.380', 'INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd02', '{}'::jsonb, NULL),
  (1410, '2023-03-24 12:06:38.494','VALINT',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd03','{"source":"platine-batch"}'::jsonb, NULL),
  (333,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd03','{}'::jsonb, NULL),
  (334,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd04','{}'::jsonb, NULL),
  (335,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd05','{}'::jsonb, NULL),
  (336,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd06','{}'::jsonb, NULL),
  (1411, '2023-03-24 12:06:38.503','PARTIELINT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd07','{"source":"platine-batch"}'::jsonb, NULL),
  (337,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd07','{}'::jsonb, NULL),
  (338,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd08','{}'::jsonb, NULL),
  (339,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd09','{}'::jsonb, NULL),
  (2620506,'2025-01-22 13:22:46.225','REFUSAL','0c83fb82-0197-7197-8e8c-a6ce2c2dbd09','{"source":"platine-gestion"}'::jsonb, NULL),
  (1412, '2023-03-24 12:06:38.508','PARTIELINT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd10','{"source":"platine-batch"}'::jsonb, NULL),
  (341,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd10','{}'::jsonb, NULL),
  (2596506,'2024-03-05 15:25:59.748','HC',      '0c83fb82-0197-7197-8e8c-a6ce2c2dbd10','{"source":"platine-gestion"}'::jsonb, NULL),
  (2596556,'2024-03-05 15:25:59.748','HC',      '0c83fb82-0197-7197-8e8c-a6ce2c2dbd10','{"source":"platine-gestion"}'::jsonb, NULL),
  (342,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd11','{}'::jsonb, NULL),
  (343,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd12','{}'::jsonb, NULL),
  (344,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd13','{}'::jsonb, NULL),
  (345,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd14','{}'::jsonb, NULL),
  (346,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd15','{}'::jsonb, NULL),
  (347,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd16','{}'::jsonb, NULL),
  (348,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd17','{}'::jsonb, NULL),
  (349,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd18','{}'::jsonb, NULL),
  (350,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd19','{}'::jsonb, NULL),
  (351,  '2023-01-11 11:05:07.380','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd20','{}'::jsonb, NULL),
  (2820, '2023-06-01 13:53:50.656','VALINT',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd20','{"source":"platine-batch"}'::jsonb, NULL),
  (1213, '2023-03-08 12:13:59.566','INITLA',    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa21','{"source":"platine-batch"}'::jsonb, NULL),
  (1217, '2023-03-08 12:25:33.960','INITLA',    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb21','{"source":"platine-batch"}'::jsonb, NULL),
  (1221, '2023-03-08 15:49:52.310','INITLA',    '0c83fb82-0197-7197-8e8c-a6ce2c2dbd21','{"source":"platine-batch"}'::jsonb, NULL),
  (2822, '2023-06-01 13:53:50.683','PARTIELINT','0c83fb82-0197-7197-8e8c-a6ce2c2dbd21','{"source":"platine-batch"}'::jsonb, NULL);

INSERT INTO public.questioning_comment(id, author, "comment", "date", questioning_id) VALUES
  (252, 'Robert', 'Commentaire 1 PROTO001', '2025-01-24 14:06:44.706', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb21'),
  (253, 'Jacqueline', 'Commentaire 2 PROTO002', '2025-01-24 14:06:55.439', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb21'),
  (254, 'Nicolas', 'Commentaire Interrogation 1208', '2025-01-24 14:07:37.855', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa21');

INSERT INTO public.questioning_communication (id, "date", status, "type", questioning_id) VALUES
  (10000, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd04'),
  (10001, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd05'),
  (10002, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd06'),
  (10003, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd07'),
  (10004, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd08'),
  (10005, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd09'),
  (10006, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd10'),
  (10007, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd11'),
  (10008, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd12'),
  (10009, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd13'),
  (10010, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd14'),
  (10011, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd15'),
  (10012, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd16'),
  (10013, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd17'),
  (10014, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd18'),
  (10015, '2024-04-15 18:21:44.298', 'AUTOMATIC', 'COURRIER_OUVERTURE','0c83fb82-0197-7197-8e8c-a6ce2c2dbd19');

INSERT INTO public."view"(id, campaign_id, id_su, identifier) VALUES
  (209, NULL, NULL, 'RESPON1'),
  (263, 'AQV2023X00', 'PROTO01', 'RESPON2'),
  (265, 'AQV2023X00', 'PROTO02', 'RESPON2'),
  (267, 'AQV2023X00', 'PROTO03', 'RESPON2'),
  (269, 'AQV2023X00', 'PROTO04', 'RESPON2'),
  (271, 'AQV2023X00', 'PROTO05', 'RESPON2'),
  (273, 'AQV2023X00', 'PROTO06', 'RESPON2'),
  (275, 'AQV2023X00', 'PROTO07', 'RESPON2'),
  (277, 'AQV2023X00', 'PROTO08', 'RESPON2'),
  (279, 'AQV2023X00', 'PROTO09', 'RESPON2'),
  (281, 'AQV2023X00', 'PROTO10', 'RESPON2'),
  (283, 'AQV2023X00', 'PROTO10', 'RESPON3'),
  (285, 'AQV2023X00', 'PROTO09', 'RESPON3'),
  (287, 'AQV2023X00', 'PROTO08', 'RESPON3'),
  (289, 'AQV2023X00', 'PROTO11', 'RESPON3'),
  (291, 'AQV2023X00', 'PROTO12', 'RESPON3'),
  (293, 'AQV2023X00', 'PROTO13', 'RESPON3'),
  (295, 'AQV2023X00', 'PROTO14', 'RESPON3'),
  (297, 'AQV2023X00', 'PROTO15', 'RESPON3'),
  (299, 'AQV2023X00', 'PROTO16', 'RESPON3'),
  (301, 'AQV2023X00', 'PROTO17', 'RESPON3'),
  (303, 'AQV2023X00', 'PROTO18', 'RESPON3'),
  (305, 'AQV2023X00', 'PROTO19', 'RESPON3'),
  (307, 'AQV2023X00', 'PROTO20', 'RESPON4'),
  (1211, 'AQV2022X00', 'PROTO21', 'RESPON5'),
  (2000, 'AQV2023X00', 'PROTO21', 'RESPON5'),
  (2001, 'AQV2024X00', 'PROTO21', 'RESPON5');

INSERT INTO public.survey_unit_comment(id, author, "comment", "date", survey_unit_id_su) VALUES
  (652, 'David', 'Commentaire UE PROTO001', '2025-01-24 14:07:07.945', 'PROTO21');

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

SELECT setval(
    'public.address_seq',
    COALESCE((SELECT MAX(id) FROM public.address), 0) + 1,
    false
);

SELECT setval(
    'public.campaign_event_seq',
    COALESCE((SELECT MAX(id) FROM public.campaign_event), 0) + 1,
    false
);

SELECT setval(
    'public.contact_event_seq',
    COALESCE((SELECT MAX(id) FROM public.contact_event), 0) + 1,
    false
);

SELECT setval(
    'public.operator_seq',
    COALESCE((SELECT MAX(id) FROM public.operator), 0) + 1,
    false
);

SELECT setval(
    'public.hibernate_sequence',
    100000,
    false
);

SELECT setval(
    'public.operator_service_seq',
    COALESCE((SELECT MAX(id) FROM public.operator_service), 0) + 1,
    false
);

SELECT setval(
    'public.quest_comment_seq',
    COALESCE((SELECT MAX(id) FROM public.questioning_comment), 0) + 1,
    false
);

SELECT setval(
    'public.questioning_accreditation_seq',
    COALESCE((SELECT MAX(id) FROM public.questioning_accreditation), 0) + 1,
    false
);

SELECT setval(
    'public.questioning_communication_seq',
    COALESCE((SELECT MAX(id) FROM public.questioning_communication), 0) + 1,
    false
);

SELECT setval(
    'public.questioning_event_seq',
    COALESCE((SELECT MAX(id) FROM public.questioning_event), 0) + 1,
    false
);

SELECT setval(
    'public.seq_upload',
    COALESCE((SELECT MAX(id) FROM public.uploads), 0) + 1,
    false
);

SELECT setval(
    'public.source_accreditation_seq',
    COALESCE((SELECT MAX(id) FROM public.source_accreditation), 0) + 1,
    false
);

SELECT setval(
    'public.su_comment_seq',
    COALESCE((SELECT MAX(id) FROM public.survey_unit_comment), 0) + 1,
    false
);

SELECT setval(
    'public.survey_unit_address_seq',
    COALESCE((SELECT MAX(id) FROM public.survey_unit_address), 0) + 1,
    false
);

SELECT setval(
    'public.user_event_seq',
    COALESCE((SELECT MAX(id) FROM public.user_event), 0) + 1,
    false
);

SELECT setval(
    'public.view_seq',
    COALESCE((SELECT MAX(id) FROM public.view), 0) + 1,
    false
);

UPDATE questioning q
SET
    highest_event_type = sub.type,
    highest_event_date = sub.date
FROM (
         SELECT DISTINCT ON (qe.questioning_id)
             qe.questioning_id,
             qe.type,
             qe.date
         FROM questioning_event qe
                  JOIN interrogation_event_order ieo
                       ON ieo.status = qe.type
         ORDER BY
             qe.questioning_id,
             ieo.event_order DESC,
             qe.date DESC
     ) AS sub
WHERE q.id = sub.questioning_id;
