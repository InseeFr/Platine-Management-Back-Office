package fr.insee.survey.datacollectionmanagement.contact.enums;

public enum GenderEnum {
  FEMALE, MALE, UNDEFINED;


  public static GenderEnum fromStringIgnoreCase(String value) {
    for (GenderEnum g : values()) {
      if (g.name().equalsIgnoreCase(value)) {
        return g;
      }
    }
    return GenderEnum.UNDEFINED;
  }

}