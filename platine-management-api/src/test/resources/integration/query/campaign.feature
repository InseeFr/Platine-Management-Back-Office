Feature: search for an Campaign

  Background:
    Given the following campaign exist:
      | IdCampaign |
      | C1         |
      | C2         |
      | C3         |

  Scenario: search for c1
    Given I am a campaign manager
    When I type "C1" in the searching campaign area by name
    Then I found the following campaign:
      | IdCampaign |
      | C1         |







