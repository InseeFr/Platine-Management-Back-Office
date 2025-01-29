Feature: search for survey

  Background:
    Given a survey exists

  Scenario: search for a survey
    Given I am an authenticated user
    When I'm searching the existing survey
    Then the survey is returned
