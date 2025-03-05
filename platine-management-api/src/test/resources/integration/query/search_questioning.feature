Feature: Search for questionings

  Background: :
    Given the source "TIC"
    Given the survey "TIC2023" related to source "TIC"
    Given the campaign "TIC2023T01" related to survey "TIC2023"
    Given the partitioning "TIC2023T0100" related to campaign "TIC2023T01"
    Given the survey unit "QSU001" with label "enterprise" and identificationName "NAME001" and identificationCode "CODE001"
    Given the survey unit "QSU002" with label "enterprise" and identificationName "NAME002" and identificationCode "CODE002"
    Given the survey unit "QSU003" with label "enterprise" and identificationName "NAME003" and identificationCode "CODE002"
    Given the survey unit "QSU004" with label "enterprise" and identificationName "NAME004" and identificationCode "CODE004"
    Given the contact "QCONTACT1" with firstname "firstname1" and lastname "lastname1" and gender "Male" and the streetnumber "17"
    Given the contact "QCONTACT2" with firstname "firstname2" and lastname "lastname2" and gender "Female" and the streetnumber "17"
    Given the contact "QCONTACT3" with firstname "firstname3" and lastname "lastname3" and gender "Female" and the streetnumber "17"
    Given the contact "QCONTACT4" with firstname "firstname4" and lastname "lastname4" and gender "Female" and the streetnumber "17"
    Given the contact "QCONTACT5" with firstname "firstname5" and lastname "lastname5" and gender "Female" and the streetnumber "17"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU001" and model "model" and main contact "QCONTACT1"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU002" and model "model" and main contact "QCONTACT2"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU003" and model "model" and main contact "QCONTACT3"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU004" and model "model" and main contact "QCONTACT3"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU004" and model "model" and contact "QCONTACT4"
    Given the questioning for partitioning "TIC2023T0100" survey unit id "QSU004" and model "model" and contact "QCONTACT5"


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

