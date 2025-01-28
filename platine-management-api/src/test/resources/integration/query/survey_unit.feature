Feature: survey unit

  Background:
    Given the following survey units exist

      | IDmetier                      | Raison sociale         |
      | Renault 1                     | 123456789        |


  Scenario: search for Renault 1
    Given I am a survey manager for survey unit
    When I get survey unit details by idSu "Renault 1"
    Then I find the surveyUnitDetail with idSu "Renault 1"
    Then I find the surveyUnitDetail with identificationName "123456789"
