Feature: search for an Campaign

  Background:
    Given the following campaign exist:
      | IdCampaign |
      | C1         |
      | C2         |
      | C3         |
    Given the source "SOURCE1"
    Given the survey "SURVEY1" related to source "SOURCE1"
    Given the campaign "CAMPAIGN1" related to survey "SURVEY1"
    Given the opened partitioning "PART1" related to campaign "CAMPAIGN1"
    Given the source "SOURCE2"
    Given the survey "SURVEY2" related to source "SOURCE2"
    Given the campaign "CAMPAIGN2" related to survey "SURVEY2"
    Given the opened partitioning "PART2" related to campaign "CAMPAIGN2"
    Given the source "SOURCE3"
    Given the survey "SURVEY3" related to source "SOURCE3"
    Given the campaign "CAMPAIGN3" related to survey "SURVEY3"
    Given the closed partitioning "PART3" related to campaign "CAMPAIGN3"
    Given the source "SOURCE4"
    Given the survey "SURVEY4" related to source "SOURCE4"
    Given the campaign "CAMPAIGN4" related to survey "SURVEY4"
    Given the opened partitioning "PART4" related to campaign "CAMPAIGN4"
    Given the survey unit "QSU001" with label "enterprise" and identificationName "NAME001" and identificationCode "CODE001"
    Given the survey unit "QSU002" with label "enterprise" and identificationName "NAME002" and identificationCode "CODE002"
    Given the survey unit "QSU003" with label "enterprise" and identificationName "NAME003" and identificationCode "CODE002"
    Given the user "USER1"
    Given the user_wallet for user "USER1" with survey_unit "QSU001" with group "GROUP_A" and source "SOURCE1"
    Given the user_wallet for user "USER1" with survey_unit "QSU001" with group "GROUP_B" and source "SOURCE3"
    Given the user_wallet for user "USER1" with survey_unit "QSU001" with group "GROUP_C" and source "SOURCE2"
    Given the user "USER2"
    Given the user_wallet for user "USER2" with survey_unit "QSU002" with group "GROUP_C" and source "SOURCE2"
    Given the user "USER3"
    Given the user_wallet for user "USER3" with survey_unit "QSU003" with group "GROUP_D" and source "SOURCE4"


  Scenario: search for c1
    Given I am a campaign manager
    When I type "C1" in the searching campaign area by name
    Then I found the following campaign
      | IdCampaign |
      | C1         |

  Scenario: Search all opening campaigns for USER1
    When I search all opening campaigns for user "USER1"
    Then the result should contain the following campaigns
      | CAMPAIGN1 |
      | CAMPAIGN2 |
      | CAMPAIGN4 |


  Scenario: Search all opening campaigns for USER2
    When I search all opening campaigns for user "USER2"
    Then the result should contain the following campaigns
      | CAMPAIGN1 |
      | CAMPAIGN2 |
      | CAMPAIGN4 |

  Scenario: Search all opening campaigns for USER3
    When I search all opening campaigns for user "USER3"
    Then the result should contain the following campaigns
      | CAMPAIGN1 |
      | CAMPAIGN2 |
      | CAMPAIGN4 |

  Scenario: Search campaigns by wallet for USER1
    When I search campaigns by wallet for user "USER1"
    Then the result should contain the following campaigns
      | CAMPAIGN1 |
      | CAMPAIGN2  |

  Scenario: Search campaigns by wallet for USER2
    When I search campaigns by wallet for user "USER2"
    Then the result should contain the following campaigns
      | CAMPAIGN2 |

  Scenario: Search campaigns by wallet for USER3
    When I search campaigns by wallet for user "USER3"
    Then the result should contain the following campaigns
      | CAMPAIGN4 |

  Scenario: Search campaigns by groups for USER1
    When I search campaigns by groups for user "USER1"
    Then the result should contain the following campaigns
      | CAMPAIGN1 |
      | CAMPAIGN2 |

  Scenario: Search campaigns by groups for USER2
    When I search campaigns by groups for user "USER2"
    Then the result should contain the following campaigns
      | CAMPAIGN2 |

  Scenario: Search campaigns by groups for USER3
    When I search campaigns by groups for user "USER3"
    Then the result should contain the following campaigns
      | CAMPAIGN4 |