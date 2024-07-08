Feature: Get Questioning Informations

  Background: :
    Given the source "EAP"
    Given the survey "EAP2023" related to source "EAP"
    Given the campaign "EAP2023T01" related to survey "EAP2023"
    Given the partitioning "EAP2023T0100" related to campaign "EAP2023T01"
    Given the survey unit "TESTCASE" with label "entreprise"
    Given the contact "USER01" with firstname "Nom" and lastanme "Prenom" and gender "Male" and the streetnumber "17"
    Given the contact "USER02" with firstname "Nom2" and lastanme "Prenom2" and gender "Female" and the streetnumber "17"
    Given the questioning for partitioning "EAP2023T0100" survey unit id "TESTCASE" and model "model" and main contact "USER01"
    Given the questioning for partitioning "EAP2023T0100" survey unit id "TESTCASE" and model "model" and contact "USER02"



  Scenario: Get informations for interviewer (main contact)
    Given the user "USER01" is authenticated as "RESPONDENT"
    When a GET request is made to "/api/questioning/informations/{idCampaign}/{idUE}" with campaign id "EAP2023T01", survey unit id "TESTCASE" and role "interviewer"
    Then the response status should be 200
    And the response content should be XML
    And the response XML should have a contact with identity "M. Nom Prenom"

  Scenario: Get informations for interviewer (not main contact)
    Given the user "USER02" is authenticated as "RESPONDENT"
    When a GET request is made to "/api/questioning/informations/{idCampaign}/{idUE}" with campaign id "EAP2023T01", survey unit id "TESTCASE" and role "interviewer"
    Then the response status should be 200
    And the response content should be XML
    And the response XML should have a contact with identity "Mme Nom2 Prenom2"

  Scenario: Get informations for reviewer
    Given the user is authenticated as "ADMIN"
    When a GET request is made to "/api/questioning/informations/{idCampaign}/{idUE}" with campaign id "EAP2023T01", survey unit id "TESTCASE" and role "reviewer"
    Then the response status should be 200
    And the response content should be XML
    And the response XML should have a contact with identity "M. Nom Prenom"
