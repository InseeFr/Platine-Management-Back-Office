Feature: Search for questionings

  Background: :
    Given the source "TIC"
    Given the survey "TIC2023" related to source "TIC"
    Given the survey "TIC2024" related to source "TIC"
    Given the campaign "TIC2023T01" related to survey "TIC2023"
    Given the campaign "TIC2024T01" related to survey "TIC2024"
    Given the partitioning "TIC2023T0100" related to campaign "TIC2023T01"
    Given the partitioning "TIC2023T0101" related to campaign "TIC2023T01"
    Given the partitioning "TIC2023T0102" related to campaign "TIC2023T01"
    Given the partitioning "TIC2024T0100" related to campaign "TIC2024T01"
    Given the survey unit "QSU001" with label "enterprise" and identificationName "NAME001" and identificationCode "CODE001"
    Given the survey unit "QSU002" with label "enterprise" and identificationName "NAME002" and identificationCode "CODE002"
    Given the survey unit "QSU003" with label "enterprise" and identificationName "NAME003" and identificationCode "CODE002"
    Given the survey unit "QSU004" with label "enterprise" and identificationName "NAME004" and identificationCode "CODE004"
    Given the survey unit "QSU005" with label "enterprise" and identificationName "NAME005" and identificationCode "CODE005"
    Given the survey unit "QSU006" with label "enterprise" and identificationName "NAME006" and identificationCode "CODE006"
    Given the contact "QCONTACT1" with firstname "firstname1" and lastname "lastname1" and gender "Male" and the streetnumber "17"
    Given the contact "QCONTACT2" with firstname "firstname2" and lastname "lastname2" and gender "Female" and the streetnumber "17"
    Given the contact "QCONTACT3" with firstname "firstname3" and lastname "lastname3" and gender "Female" and the streetnumber "17"
    Given the contact "QCONTACT4" with firstname "firstname4" and lastname "lastname4" and gender "Female" and the streetnumber "17"
    Given the contact "QCONTACT5" with firstname "firstname5" and lastname "lastname5" and gender "Female" and the streetnumber "17"
    Given the contact "QCONTACT6" with firstname "firstname6" and lastname "lastname6" and gender "Female" and the streetnumber "17"
    Given the contact "QCONTACT7" with firstname "firstname7" and lastname "lastname7" and gender "Female" and the streetnumber "17"
    Given the contact "QCONTACT8" with firstname "firstname8" and lastname "lastname8" and gender "Female" and the streetnumber "17"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU001" and model "model" and main contact "QCONTACT1"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU002" and model "model" and main contact "QCONTACT2"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU003" and model "model" and main contact "QCONTACT3"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU004" and model "model" and main contact "QCONTACT3"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU004" and model "model" and contact "QCONTACT4"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU004" and model "model" and contact "QCONTACT5"
    Given the questioning 0 for partitioning "TIC2023T0100" and survey unit id "QSU005" and model "model"
    Given the questioning 1 for partitioning "TIC2023T0101" and survey unit id "QSU005" and model "model"
    Given the questioning 2 for partitioning "TIC2023T0102" and survey unit id "QSU005" and model "model"
    Given the questioning 3 for partitioning "TIC2024T0100" and survey unit id "QSU006" and model "model"
    Given the questioning event for questioning 0 with type "INITLA" and date "2025-06-20T20:00:00"
    Given the questioning event for questioning 0 with type "VALINT" and date "2025-06-20T20:00:00"
    Given the questioning event for questioning 0 with type "PARTIELINT" and date "2025-06-20T20:01:00"
    Given the questioning event for questioning 1 with type "INITLA" and date "2025-06-20T20:00:00"
    Given the questioning event for questioning 1 with type "VALPAP" and date "2025-06-20T20:02:00"
    Given the questioning event for questioning 2 with type "PARTIELINT" and date "2025-06-20T20:01:00"
    Given the questioning event for questioning 2 with type "INITLA" and date "2025-06-20T20:00:00"
    Given the questioning event for questioning 3 with type "INITLA" and date "2025-07-20T20:00:00"
    Given the questioning event for questioning 3 with type "PARTIELINT" and date "2025-07-20T20:01:00"
    Given the questioning communication for questioning 0 with type "COURRIER_OUVERTURE" and date "2025-06-20T20:00:00"
    Given the questioning communication for questioning 0 with type "MAIL_RELANCE" and date "2025-06-20T20:01:00"
    Given the questioning communication for questioning 1 with type "COURRIER_OUVERTURE" and date "2025-06-20T20:02:00"
    Given the questioning communication for questioning 2 with type "COURRIER_OUVERTURE" and date "2025-06-20T20:00:00"
    Given the questioning communication for questioning 2 with type "MAIL_RELANCE" and date "2025-06-20T20:02:00"
    Given the questioning communication for questioning 3 with type "COURRIER_OUVERTURE" and date "2025-06-20T20:00:00"
    Given the questioning communication for questioning 3 with type "COURRIER_RELANCE" and date "2025-07-20T20:02:00"

  Scenario: Search by surveyUnitId
    When I search for Questioning with "QSU001" and page 0 with size 10
    Then the result size is 1
    Then the result should contain the following Questioning related to surveyUnit:
      | id     | listContacts |
      | QSU001 | QCONTACT1    |


  Scenario: Search by surveyUnitName
    When I search for Questioning with "NAME002" and page 0 with size 10
    Then the result size is 1
    Then the result should contain the following Questioning related to surveyUnit:
      | id     | listContacts |
      | QSU002 | QCONTACT2    |

  Scenario: Search by surveyUnitCode
    When I search for Questioning with "CODE002" and page 0 with size 10
    Then the result size is 2
    Then the result should contain the following Questioning related to surveyUnit:
      | id     | listContacts |
      | QSU002 | QCONTACT2    |
      | QSU003 | QCONTACT3    |


  Scenario: Search by accreditationContactId
    When I search for Questioning with "QCONTACT3" and page 0 with size 10
    Then the result size is 2
    Then the result should contain the following Questioning related to surveyUnit:
      | id     | listContacts                    |
      | QSU003 | QCONTACT3                       |
      | QSU004 | QCONTACT3,QCONTACT4,QCONTACT5|


  Scenario: Search by highest questioning event
    When I search questionings for campaign "TIC2023T01" and highest event types
      | VALINT |
      | VALPAP |
    Then the result should contain the following questionings
      | id | surveyUnitId | validationDate      | highestEventType | lastCommunicationType |
      | 1  | QSU005       | 2025-06-20T20:02:00 | VALPAP           | COURRIER_OUVERTURE    |

  Scenario: Search by last communication types
    When I search questionings for campaign "TIC2023T01" and last communication types
      | MAIL_RELANCE       |
      | COURRIER_OUVERTURE |
    Then the result should contain the following questionings
      | id | surveyUnitId | validationDate      | highestEventType | lastCommunicationType |
      | 0  | QSU005       |                     | PARTIELINT       | MAIL_RELANCE          |
      | 1  | QSU005       | 2025-06-20T20:02:00 | VALPAP           | COURRIER_OUVERTURE    |
      | 2  | QSU005       |                     | PARTIELINT       | MAIL_RELANCE          |


  Scenario: Search by last communication type and highest type event
    When I search questionings for campaign "TIC2024T01" and highest event type "PARTIELINT" and last communication type "COURRIER_RELANCE"
    Then the result should contain the following questionings
      | id | surveyUnitId | validationDate | highestEventType | lastCommunicationType |
      | 3  | QSU006       |                | PARTIELINT       | COURRIER_RELANCE      |






