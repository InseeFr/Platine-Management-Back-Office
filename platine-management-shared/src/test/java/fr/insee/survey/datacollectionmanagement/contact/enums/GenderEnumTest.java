package fr.insee.survey.datacollectionmanagement.contact.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenderEnumTest {

  @Test
  void testFromStringIgnoreCase_Female() {
    assertEquals(GenderEnum.FEMALE, GenderEnum.fromStringIgnoreCase("Female"));
    assertEquals(GenderEnum.FEMALE, GenderEnum.fromStringIgnoreCase("female"));
    assertEquals(GenderEnum.FEMALE, GenderEnum.fromStringIgnoreCase("FEMALE"));
  }

  @Test
  void testFromStringIgnoreCase_Male() {
    assertEquals(GenderEnum.MALE, GenderEnum.fromStringIgnoreCase("Male"));
    assertEquals(GenderEnum.MALE, GenderEnum.fromStringIgnoreCase("male"));
    assertEquals(GenderEnum.MALE, GenderEnum.fromStringIgnoreCase("MALE"));
  }

  @Test
  void testFromStringIgnoreCase_Undefined() {
    assertEquals(GenderEnum.UNDEFINED, GenderEnum.fromStringIgnoreCase("Other"));
    assertEquals(GenderEnum.UNDEFINED, GenderEnum.fromStringIgnoreCase(""));
    assertEquals(GenderEnum.UNDEFINED, GenderEnum.fromStringIgnoreCase(null));
  }
}
