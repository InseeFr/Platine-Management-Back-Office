Feature: Search for a contact

  Background:
    Given the following contacts exist:
      | idep   | lastname     | firstname     | email              |
      | JD2024 | Doe          | John          | john.doe@gmail.com |
      | DD1234 | Durant       | Doeris        | dd1995@orange.fr   |
      | ABCD12 | DOEDOE       | johnny        | johjodu94@yahoo.fr |
      | DOE203 |              |               |                    |
      | COCO54 | BOOP         | Betty         | betty.boop@free.fr |
      | RDHGFP | D'avignon    | Léa           | lea@free.fr        |
      | DPJ8P6 | Pierre-Henri | Jean-François | jfph@free.fr       |
      | PLA97W | Martin       | Pierre        | pm@free.fr         |


  Scenario: search for John Doe
    Given I am a survey manager
    When I type "Joh" in the searching contact area by email
    Then I found the following contacts:
      | idep   | lastname | firstname | email              |
      | ABCD12 | DOEDOE   | johnny    | johjodu94@yahoo.fr |
      | JD2024 | Doe      | John      | john.doe@gmail.com |

  Scenario: search for a contact who has a name or surname beginning by Doe
    Given I am a survey manager
    When I type "Doe" in the searching contact area by name
    Then I found the following contacts:
      | idep   | lastname | firstname | email              |
      | JD2024 | Doe      | John      | john.doe@gmail.com |
      | DD1234 | Durant   | Doeris    | dd1995@orange.fr   |
      | ABCD12 | DOEDOE   | johnny    | johjodu94@yahoo.fr |

  Scenario: search for a contact who does not exist
    Given I am a survey manager
    When I type "Cam" in the searching contact area by identifier
    When I type "Cam" in the searching contact area by name
    When I type "Cam" in the searching contact area by email
    Then I found no contact

  Scenario: search for betty boop
    Given I am a survey manager
    When I type "betty boop" in the searching contact area by name
    Then I found the following contacts:
      | idep   | lastname | firstname | email              |
      | COCO54 | BOOP     | Betty     | betty.boop@free.fr |

  Scenario: search for name with accent
    Given I am a survey manager
    When I type "léa" in the searching contact area by name
    Then I found the following contacts:
      | idep   | lastname  | firstname | email       |
      | RDHGFP | D'avignon | Léa       | lea@free.fr |

  Scenario: search for name with cedilla
    Given I am a survey manager
    When I type "Jean-Franç" in the searching contact area by name
    Then I found the following contacts:
      | idep   | lastname     | firstname     | email        |
      | DPJ8P6 | Pierre-Henri | Jean-François | jfph@free.fr |

  Scenario: search for name or firstname
    Given I am a survey manager
    When I type "pierre" in the searching contact area by name
    Then I found the following contacts:
      | idep   | lastname     | firstname     | email        |
      | DPJ8P6 | Pierre-Henri | Jean-François | jfph@free.fr |
      | PLA97W | Martin       | Pierre        | pm@free.fr   |

  Scenario: search for John Doe by his "idep"
    Given I am a survey manager
    When I type "JD2" in the searching contact area by identifier
    Then I found the following contacts:
      | idep   | lastname | firstname | email              |
      | JD2024 | Doe      | John      | john.doe@gmail.com |