Feature: search for a contact

  I am searching for a specific contact

  Background: Contact

  | Nom    | Prénom | Idep   | Adresse mél        |
  |--------|--------|--------|--------------------|
  | Doe    | John   | JD2024 | john.doe@gmail.com |
  | Durant | Doeris | DD1234 | dd1995@orange.fr   |
  | DOEDOE | johnny | ABCD12 | jojodu94@yahoo.fr  |
  |        |        | DOE203 |                    |
  | BOOP   | Betty  | COCO54 | betty.boop@free.fr |

  Scenario: search for John Doe
    Given I am a survey manager who's searching for "John Doe"
    When i type "Joh" in the name and surname searching area
    Then i found "Johnny"
    And i found "John"

  Scenario: search for a contact who has a name or surname beginning by Doe
    Given I am a survey manager who's searching for "Doe"
    When i type "Doe" in the name and surname searching area
    Then i found "Johnny"
    And i found "John"
    And i found "Doeris"

  Scenario: search for a contact who does not exist
    Given I am a survey manager who's searching for "Camille"
    When i type "Cam" in the name and surname searching area
    Then i found nothing

  Scenario: search for betty boop
    Given I am a survey manager who's searching for "Betty Boop"
    When i type "bet" in the email searching area
    Then i found "betty.boop@free.fr"

  Scenario: search for John Doe by his "idep"
    Given I am a survey manager who's searching for "John Doe"
    When i type "JD2" in the idep searching area
    Then i found "JD2024"
