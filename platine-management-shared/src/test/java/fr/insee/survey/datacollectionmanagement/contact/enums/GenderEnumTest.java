package fr.insee.survey.datacollectionmanagement.contact.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenderEnumTest {

  @Test
  void testFromStringIgnoreCase_Female() {
    assertEquals(GenderEnum.Female, GenderEnum.fromStringIgnoreCase("Female"));
    assertEquals(GenderEnum.Female, GenderEnum.fromStringIgnoreCase("female"));
    assertEquals(GenderEnum.Female, GenderEnum.fromStringIgnoreCase("FEMALE"));
  }

  @Test
  void testFromStringIgnoreCase_Male() {
    assertEquals(GenderEnum.Male, GenderEnum.fromStringIgnoreCase("Male"));
    assertEquals(GenderEnum.Male, GenderEnum.fromStringIgnoreCase("male"));
    assertEquals(GenderEnum.Male, GenderEnum.fromStringIgnoreCase("MALE"));
  }

  @Test
  void testFromStringIgnoreCase_Undefined() {
    assertEquals(GenderEnum.Undefined, GenderEnum.fromStringIgnoreCase("Other"));
    assertEquals(GenderEnum.Undefined, GenderEnum.fromStringIgnoreCase(""));
    assertEquals(GenderEnum.Undefined, GenderEnum.fromStringIgnoreCase(null));
  }
}
