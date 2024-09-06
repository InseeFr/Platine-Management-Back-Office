Feature: search for an UE

  Background:
    Given the following survey units exist

      | Raison sociale                | IDmetier         |
      | ----------------------------- | ---------------- |
      | Renault 1                     | 123456789        |
      | Renault 2                     | 12345678900015   |
      | Petite entreprise familiale   | 36985214700001   |
      | coiffeur                      | 147852369        |
      | peugeot                       | 258741369        |
      | asso pour les animaux         | asso123          |

  Scenario: search for Renault 1
    Given I am a survey manager for survey unit
    When I type "Ren" in the searching survey unit area by name
    Then I found the following SU:
      | Raison sociale | IDmetier       |
      | Renault 1      | 123456789      |
      | Renault 2      | 12345678900015 |

  Scenario: search for Renault 1
    Given I am a survey manager for survey unit
    When I type "123456789" in the searching survey unit area by code
    Then I found the following SU:
      | Raison sociale | IDmetier       |
      | Renault 1      | 123456789      |
      | Renault 2      | 12345678900015 |

  Scenario: search for an UE who does not exist
    Given I am a survey manager for survey unit
    When I type "Cam" in the searching survey unit area by name
    Then I found no survey unit

  Scenario: search for an asso by raison sociale
    Given I am a survey manager for survey unit
    When I type "asso" in the searching survey unit area by name
    Then I found the following SU:
      | Raison sociale        | IDmetier |
      | asso pour les animaux | asso123  |

  Scenario: search for an asso by IDmetier
    Given I am a survey manager for survey unit
    When I type "asso" in the searching survey unit area by code
    Then I found the following SU:
      | Raison sociale        | IDmetier |
      | asso pour les animaux | asso123  |
