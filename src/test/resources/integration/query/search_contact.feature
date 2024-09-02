Feature: Search for a contact

  Background:
    Given the following contacts exist:
      | idep   | lastname | firstname | email              |
      | JD2024 | Doe      | John      | john.doe@gmail.com |
      | DD1234 | Durant   | Doeris    | dd1995@orange.fr   |
      | ABCD12 | DOEDOE   | johnny    | johjodu94@yahoo.fr  |
      | DOE203 |          |           |                    |
      | COCO54 | BOOP     | Betty     | betty.boop@free.fr |

  Scenario: search for John Doe
    Given I am a survey manager
    When I type "Joh" in the searching area by email
    Then I found the following contacts:
      | idep   | lastname | firstname | email              |
      | ABCD12 | DOEDOE   | johnny    | johjodu94@yahoo.fr  |
      | JD2024 | Doe      | John      | john.doe@gmail.com |

  Scenario: search for a contact who has a name or surname beginning by Doe
    Given I am a survey manager
    When I type "Doe" in the searching area by name
    Then I found the following contacts:
      | idep   | lastname | firstname | email              |
      | JD2024 | Doe      | John      | john.doe@gmail.com |
      | DD1234 | Durant   | Doeris    | dd1995@orange.fr   |
      | ABCD12 | DOEDOE   | johnny    | johjodu94@yahoo.fr  |

  Scenario: search for a contact who does not exist
    Given I am a survey manager
    When I type "Cam" in the searching area by identifier
    When I type "Cam" in the searching area by name
    When I type "Cam" in the searching area by email
    Then I found nothing

  Scenario: search for betty boop
    Given I am a survey manager
    When I type "betty boop" in the searching area by name
    Then I found the following contacts:
      | idep   | lastname | firstname | email              |
      | COCO54 | BOOP     | Betty     | betty.boop@free.fr |

  Scenario: search for John Doe by his "idep"
    Given I am a survey manager
    When I type "JD2" in the searching area by identifier
    Then I found the following contacts:
      | idep   | lastname | firstname | email              |
      | JD2024 | Doe      | John      | john.doe@gmail.com |