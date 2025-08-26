                                -- E2E tests sources --

INSERT INTO public."source"
(id, long_wording, mandatory_my_surveys, periodicity, short_wording, type)
VALUES
('SOURCE_1', 'Source 1 containing a household campaign with one interrogation for E2E_RESPON_1', false, 'X', 'One interrogation', 'HOUSEHOLD'),
('SOURCE_2', 'Source 2 containing a business campaign with multiple interrogations and one SU for E2E_RESPON_2', false, 'X', 'Multi-interrogations, one SU', 'BUSINESS'),
('SOURCE_3', 'Source 3 containing a business campaign with multiple interrogations and SUs for E2E_RESPON_3 and E2E_RESPON_4', false, 'X', 'Multi-interrogations, one SU', 'BUSINESS'),
('SOURCE_4', 'Source 4 containing a business campaign with one UE and interrogation for E2E_RESPON_4', false, 'X', 'Multi-interrogations, one SU', 'BUSINESS'),
-- TODO : ('SOURCE_5', 'Source 1 containing a household campaign with mutliples interrogations and SUs for a contact', false, 'X', 'One interrogation', 'HOUSEHOLD'),
('SOURCE_R2D2', 'Source R2D2 containing a business campaign with multiple sources and UEs for E2E_RESPON_3', false, 'X', 'Multi-SU R2D2', 'BUSINESS'),
('eec', 'Source EEC for E2E_RESPON_EEC', false, 'X', 'EEC', 'HOUSEHOLD'),
('SOURCE_CLOSED', 'Source closed containing a closed campaign or E2E_RESPON_CLOSED', false, 'X', 'Closed', 'HOUSEHOLD');

                                 -- E2E tests survey --

INSERT INTO public.survey
(id, cnis_url, communication, diffusion_url, long_objectives, long_wording, notice_url, sample_size, short_objectives, short_wording, specimen_url, visa_number, year_value, source_id)
VALUES
('E2E_SURVEY_1', 'http://cnis/E2E_SURVEY_1', '', 'http://diffusion/E2E_SURVEY_1', 'This survey is used for E2E testing, it is inherited from SOURCE_1. It has a campaign with only one interrogation for a contact', 'E2E Testing one interrogation survey', 'http://notice/AQV2023', 0, 'This survey is used for E2E testing', 'Data for E2E testing', 'http://specimenUrl/AQV2022', '2025xxxxxx', 2025, 'SOURCE_1'),
('E2E_SURVEY_2', 'http://cnis/E2E_SURVEY_2', '', 'http://diffusion/E2E_SURVEY_2',
       'This survey is used for E2E testing, it is inherited from SOURCE_2. It has a campaign with multiple interrogations and one SU for a contact', 'E2E Survey 2', 'http://notice/E2E_SURVEY_2', 0,
       'Short objectives for E2E_SURVEY_2', 'Short wording E2E_SURVEY_2', 'http://specimen/E2E_SURVEY_2',
       '2025xxxxx', 2025, 'SOURCE_2'),
('E2E_SURVEY_3', 'http://cnis/E2E_SURVEY_3', '', 'http://diffusion/E2E_SURVEY_3',
    'This survey is used for E2E testing, it is inherited from SOURCE_3. It has a campaign with multiple interrogations and SUs', 'E2E Survey 3', 'http://notice/E2E_SURVEY_3', 0,
    'Short objectives for E2E_SURVEY_3', 'Short wording E2E_SURVEY_3', 'http://specimen/E2E_SURVEY_3',
    '2025xxxxx', 2025, 'SOURCE_3'),
('E2E_SURVEY_4', 'http://cnis/E2E_SURVEY_4', '', 'http://diffusion/E2E_SURVEY_4',
       'This survey is used for E2E testing, it is inherited from SOURCE_4. It has a campaign with multiple interrogations and SUs', 'E2E Survey 4', 'http://notice/E2E_SURVEY_4', 0,
       'Short objectives for E2E_SURVEY_4', 'Short wording E2E_SURVEY_4', 'http://specimen/E2E_SURVEY_4',
       '2025xxxxx', 2025, 'SOURCE_4'),
('E2E_SURVEY_R2D2', 'http://cnis/E2E_SURVEY_R2D2', '', 'http://diffusion/E2E_SURVEY_R2D2',
       'This R2D2 survey is used for E2E testing, it is inherited from SOURCE_R2D2. It has a campaign with multiple interrogations and SUs', 'E2E Survey 4', 'http://notice/E2E_SURVEY_R2D2', 0,
       'Short objectives for E2E_SURVEY_R2D2', 'Short wording E2E_SURVEY_R2D2', 'http://specimen/E2E_SURVEY_R2D2',
       '2025xxxxx', 2025, 'SOURCE_R2D2'),
('E2E_SURVEY_EEC', 'http://cnis/E2E_SURVEY_EEC', '', 'http://diffusion/E2E_SURVEY_EEC',
       'This survey is used for E2E testing, it is inherited from eec', 'E2E Survey EEC', 'http://notice/E2E_SURVEY_EEC', 0,
       'Short objectives for E2E_SURVEY_EEC', 'Short wording E2E_SURVEY_EEC', 'http://specimen/E2E_SURVEY_EEC',
       '2025xxxxx', 2025, 'eec'),
('E2E_SURVEY_CLOSED', 'http://cnis/E2E_SURVEY_CLOSED', '', 'http://diffusion/E2E_SURVEY_CLOSED',
       'This survey is used for E2E testing, it is inherited from SOURCE_CLOSED', 'E2E Survey CLOSED', 'http://notice/E2E_SURVEY_CLOSED', 0,
       'Short objectives for E2E_SURVEY_CLOSED', 'Short wording E2E_SURVEY_CLOSED', 'http://specimen/E2E_SURVEY_CLOSED',
       '2025xxxxx', 2025, 'SOURCE_CLOSED');

                                 -- E2E tests campaigns --

INSERT INTO public.campaign
(id, campaign_wording, survey_id, period_value, year_value, datacollection_target, sensitivity, operation_upload_reference)
VALUES
('E2E_CAMPAIGN_1', 'E2E Testing Household Campaign for a contact with only one interrogation existing in its partition', 'E2E_SURVEY_1', 'X00', 2025, 'LUNATIC_NORMAL', false, null),
('E2E_CAMPAIGN_2', 'E2E Testing Business Campaign for a contact with multiple interrogations and one SU', 'E2E_SURVEY_2', 'X00', 2025, 'LUNATIC_NORMAL', false, null),
('E2E_CAMPAIGN_3', 'E2E Testing Business Campaign for a contact with multiple interrogations SUs', 'E2E_SURVEY_3', 'X00', 2025, 'XFORM1', false, null),
('E2E_CAMPAIGN_4', 'E2E Testing Business Campaign for a contact with one interrogation and one SU', 'E2E_SURVEY_4', 'X00', 2025, 'LUNATIC_SENSITIVE', false, null),
('E2E_CAMPAIGN_R2D2', 'E2E R2D2 Business Testing Campaign for a contact with multiple interrogations SUs', 'E2E_SURVEY_R2D2', 'X00', 2025, 'FILE_UPLOAD', false, 'op upload reference'),
('E2E_CAMPAIGN_EEC', 'E2E Testing Campaign EEC', 'E2E_SURVEY_EEC', 'X00', 2025, 'LUNATIC_NORMAL', false, null),
('E2E_CAMPAIGN_CLOSED', 'E2E Testing Campaign CLOSED', 'E2E_SURVEY_CLOSED', 'X00', 2025, 'LUNATIC_NORMAL', false, null);

                  -- E2E tests partitioning --

INSERT INTO public.partitioning
(id, closing_date, opening_date, return_date, campaign_id, "label")
VALUES
('PART1', '2099-12-30 01:00:00.000', '2023-03-07 01:00:00.000', '2099-12-31 10:33:27.723',  'E2E_CAMPAIGN_1', 'E2E Partition 1 Label'),
('PART2', '2099-12-30 01:00:00.000', '2023-03-07 01:00:00.000', '2099-12-31 10:33:27.723',  'E2E_CAMPAIGN_2', 'E2E Partition 2 Label'),
('PART3', '2099-12-30 01:00:00.000', '2023-03-07 01:00:00.000', '2099-12-31 10:33:27.723',  'E2E_CAMPAIGN_3', 'E2E Partition 3 Label'),
('PART4', '2099-12-30 01:00:00.000', '2023-03-07 01:00:00.000', '2099-12-31 10:33:27.723',  'E2E_CAMPAIGN_4', 'E2E Partition 4 Label'),
('PARTR2D2', '2099-12-30 01:00:00.000', '2023-03-07 01:00:00.000', '2099-12-31 10:33:27.723',  'E2E_CAMPAIGN_R2D2', 'E2E Partition R2D2 Label'),
('PARTEEC', '2099-12-30 01:00:00.000', '2023-03-07 01:00:00.000', '2099-12-31 10:33:27.723',  'E2E_CAMPAIGN_EEC', 'E2E Partition EEC Label'),
('PARTCLOSED', '2020-12-30 01:00:00.000', '2020-03-07 01:00:00.000', '2020-12-31 10:33:27.723',  'E2E_CAMPAIGN_CLOSED', 'E2E Partition CLOSED Label');


                    -- E2E tests adress --

INSERT INTO public.address
(id,  country_name, street_name, street_number, zip_code, address_supplement, cedex_code, cedex_name, city_name, country_code, repetition_index, special_distribution, street_type)
VALUES
(1, 'France', 'rue des oies', '1', '75000', NULL, NULL, NULL, 'Paris', NULL, NULL, NULL, NULL);

         -- E2E tests contacts --

INSERT INTO public.contact
(identifier, "comment", email, first_name, "function", gender, last_name, phone, address_id, external_id, email_verify,  usual_company_name, phone2)
VALUES
('E2E_RESPON_1',   'No comment', 'e2erespon1@insee.fr', 'Keith', '', 'Male', 'Cozart', '0600000000', 1, NULL, false,  NULL, NULL),
('E2E_RESPON_2',   'No comment', 'e2erespon2@insee.fr', 'Jaylah', 'CEO', 'Female', 'Hickmon', '0600000000', 1, NULL, false,  'Def Jam Records', NULL),
('E2E_RESPON_3',   'No comment', 'e2erespon3@insee.fr', 'Patrick Earl', 'CEO', 'Male', 'Houston', '0600000000', 1, NULL, false,  'Ghetty Green Inc.', NULL),
('E2E_RESPON_4',   'No comment', 'e2erespon4@insee.fr', 'Dana', 'Producer', 'Female', 'Owens', '0600000000', 1, NULL, false,  '', NULL),
('E2E_RESPON_EEC', 'No comment', 'e2erespon_eec@insee.fr', 'Nayvadius', '', 'Male', 'Cash', '0600000000', 1, NULL, false,  '', NULL),
('E2E_RESPON_CLOSED', 'No comment', 'e2erespon_closed@insee.fr', 'Skye', '', 'Female', 'Edwards', '0600000000', 1, NULL, false,  '', NULL);

                                 -- E2E tests contacts events --

INSERT INTO public.contact_event
(id, event_date, "type", contact_identifier, payload)
VALUES
(1, '2023-03-21 16:19:45.648', 1, 'E2E_RESPON_1', NULL),
(2, '2023-03-21 16:19:45.648', 2, 'E2E_RESPON_2', NULL),
(3, '2023-03-21 16:19:45.648', 3, 'E2E_RESPON_3', NULL),
(4, '2023-03-21 16:19:45.648', 3, 'E2E_RESPON_EEC', NULL),
(5, '2023-03-21 16:19:45.648', 3, 'E2E_RESPON_CLOSED', NULL),
(6, '2023-03-21 16:19:45.648', 4, 'E2E_RESPON_4', NULL);

                                 -- E2E tests survey unit --

INSERT INTO public.survey_unit
(id_su, identification_name, identification_code, survey_unit_address_id, "label")
VALUES
('E2E_SU_1', '', '', NULL, NULL),
('E2E_SU_2', 'Def Jam Records', '1', NULL, NULL),
('E2E_SU_3', 'Ghetty Green Inc.', '2', NULL, NULL),
('E2E_SU_4', 'G59', '3', NULL, NULL),
('E2E_SU_5', 'Don Dada Records', '4', NULL, NULL),
('E2E_SU_6', 'Death Row Records', '5', NULL, NULL),
('E2E_SU_7', 'Aftermath Entertainment', '6', NULL, NULL),
('E2E_SU_8', 'Almighty So Inc.', '7', NULL, NULL),
('E2E_SU_9', 'All Screwed Up', '8', NULL, NULL),
('E2E_SU_10','All Screwed Up', '8', NULL, NULL),
('E2E_SU_11', 'Only 2 Left Alive', '9', NULL, NULL),
('E2E_SU_EEC', '', '', NULL, NULL),
('E2E_SU_CLOSED', '', '', NULL, NULL);

                                -- E2E tests questioning --

INSERT INTO public.questioning
(id, old_id, id_partitioning, model_name, survey_unit_id_su)
VALUES
('00000000-0000-0000-0000-000000000001', 1, 'PART1', 'Interrogation 1', 'E2E_SU_1'),
('00000000-0000-0000-0000-000000000002', 2, 'PART2', 'Interrogation 2', 'E2E_SU_2'),
('00000000-0000-0000-0000-000000000003', 3, 'PART2', 'Interrogation 3', 'E2E_SU_2'),
('00000000-0000-0000-0000-000000000004', 4, 'PART3', 'Interrogation 4', 'E2E_SU_2'),
('00000000-0000-0000-0000-000000000005', 5, 'PART3', 'Interrogation 5', 'E2E_SU_3'),
('00000000-0000-0000-0000-000000000006', 6, 'PART3', 'Interrogation 6', 'E2E_SU_4'),
('00000000-0000-0000-0000-000000000007', 7, 'PARTR2D2', 'Interrogation 7', 'E2E_SU_5'),
('00000000-0000-0000-0000-000000000008', 8, 'PARTR2D2', 'Interrogation 8', 'E2E_SU_6'),
('00000000-0000-0000-0000-000000000009', 9, 'PARTR2D2', 'Interrogation 9', 'E2E_SU_7'),
('00000000-0000-0000-0000-000000000010', 10, 'PARTEEC', 'Interrogation 10 EEC', 'E2E_SU_EEC'),
('00000000-0000-0000-0000-000000000011', 11, 'PARTCLOSED', 'Interrogation 11 CLOSED', 'E2E_SU_EEC'),
('00000000-0000-0000-0000-000000000012', 12, 'PART3', 'Interrogation 12', 'E2E_SU_8'),
('00000000-0000-0000-0000-000000000013', 13, 'PART3', 'Interrogation 13', 'E2E_SU_9'),
('00000000-0000-0000-0000-000000000014', 14, 'PART3', 'Interrogation 14', 'E2E_SU_10'),
('00000000-0000-0000-0000-000000000015', 15, 'PART4', 'Interrogation 15', 'E2E_SU_11');




                                -- E2E tests questioning accreditation --

INSERT INTO public.questioning_accreditation
(questioning_id, creation_author, creation_date, id_contact, is_main, id)
VALUES
('00000000-0000-0000-0000-000000000001', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_1', true, 1),
('00000000-0000-0000-0000-000000000002', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_2', true, 2),
('00000000-0000-0000-0000-000000000003', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_2', true, 3),
('00000000-0000-0000-0000-000000000004', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_3', true, 4),
('00000000-0000-0000-0000-000000000005', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_3', true, 5),
('00000000-0000-0000-0000-000000000006', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_3', true, 6),
('00000000-0000-0000-0000-000000000007', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_3', true, 7),
('00000000-0000-0000-0000-000000000008', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_3', true, 8),
('00000000-0000-0000-0000-000000000009', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_3', true, 9),
('00000000-0000-0000-0000-000000000010', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_EEC', true, 10),
('00000000-0000-0000-0000-000000000011', 'platine-batch', '2020-03-08 15:49:51.670', 'E2E_RESPON_CLOSED', true, 11),
('00000000-0000-0000-0000-000000000012', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_4', true, 12),
('00000000-0000-0000-0000-000000000013', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_4', true, 13),
('00000000-0000-0000-0000-000000000014', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_4', true, 14),
('00000000-0000-0000-0000-000000000015', 'platine-batch', '2025-03-08 15:49:51.670', 'E2E_RESPON_4', true, 15);




                                -- E2E tests questioning event --

INSERT INTO public.questioning_event
(id, "date", "type", questioning_id, payload, id_upload)
VALUES
(1,  '2025-01-11 11:05:07.380', 'PARTIELINT', '00000000-0000-0000-0000-000000000001', '{}'::jsonb, NULL),
(2,  '2025-01-11 11:05:07.380', 'INITLA',     '00000000-0000-0000-0000-000000000002', '{}'::jsonb, NULL),
(3,  '2025-01-11 11:05:07.380', 'PARTIELINT', '00000000-0000-0000-0000-000000000003', '{}'::jsonb, NULL),
(4,  '2025-01-11 11:05:07.380', 'VALINT',     '00000000-0000-0000-0000-000000000004', '{}'::jsonb, NULL),
(5,  '2025-01-11 11:05:07.380', 'INITLA',     '00000000-0000-0000-0000-000000000005', '{}'::jsonb, NULL),
(6,  '2025-01-11 11:05:07.380', 'PARTIELINT', '00000000-0000-0000-0000-000000000006', '{}'::jsonb, NULL),
(7,  '2025-01-11 11:05:07.380', 'INITLA',     '00000000-0000-0000-0000-000000000007', '{}'::jsonb, NULL),
(8,  '2025-01-11 11:05:07.380', 'PARTIELINT', '00000000-0000-0000-0000-000000000008', '{}'::jsonb, NULL),
(9,  '2025-01-11 11:05:07.380', 'REFUSAL',    '00000000-0000-0000-0000-000000000009', '{}'::jsonb, NULL),
(10, '2025-01-11 11:05:07.380', 'PARTIELINT', '00000000-0000-0000-0000-000000000010', '{}'::jsonb, NULL),
(11, '2025-01-11 11:05:07.380', 'PARTIELINT', '00000000-0000-0000-0000-000000000011', '{}'::jsonb, NULL),
(12, '2025-01-11 11:05:07.380', 'PARTIELINT', '00000000-0000-0000-0000-000000000012', '{}'::jsonb, NULL),
(13, '2025-01-11 11:05:07.380', 'INITLA', '00000000-0000-0000-0000-000000000013', '{}'::jsonb, NULL),
(14, '2025-01-11 11:05:07.380', 'WASTED', '00000000-0000-0000-0000-000000000014', '{}'::jsonb, NULL),
(15, '2025-01-11 11:05:07.380', 'PARTIELINT', '00000000-0000-0000-0000-000000000015', '{}'::jsonb, NULL);



                                -- E2E tests views --

INSERT INTO public.view
(id, campaign_id, id_su, identifier)
VALUES
(1, 'E2E_CAMPAIGN_1', 'E2E_SU_1', 'E2E_RESPON_1'),
(2, 'E2E_CAMPAIGN_2', 'E2E_SU_2', 'E2E_RESPON_2'),
(3, 'E2E_CAMPAIGN_3', 'E2E_SU_2', 'E2E_RESPON_3'),
(4, 'E2E_CAMPAIGN_3', 'E2E_SU_3', 'E2E_RESPON_3'),
(5, 'E2E_CAMPAIGN_3', 'E2E_SU_4', 'E2E_RESPON_3'),
(6, 'E2E_CAMPAIGN_R2D2', 'E2E_SU_5', 'E2E_RESPON_3'),
(7, 'E2E_CAMPAIGN_R2D2', 'E2E_SU_6', 'E2E_RESPON_3'),
(8, 'E2E_CAMPAIGN_R2D2', 'E2E_SU_7', 'E2E_RESPON_3'),
(9, 'E2E_CAMPAIGN_EEC', 'E2E_SU_EEC', 'E2E_RESPON_EEC'),
(10, 'E2E_CAMPAIGN_3', 'E2E_SU_8', 'E2E_RESPON_4'),
(11, 'E2E_CAMPAIGN_3', 'E2E_SU_9', 'E2E_RESPON_4'),
(10, 'E2E_CAMPAIGN_3', 'E2E_SU_10', 'E2E_RESPON_4'),
(10, 'E2E_CAMPAIGN_4', 'E2E_SU_11', 'E2E_RESPON_4');