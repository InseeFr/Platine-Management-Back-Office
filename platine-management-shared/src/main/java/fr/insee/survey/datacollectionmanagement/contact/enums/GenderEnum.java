package fr.insee.survey.datacollectionmanagement.contact.enums;

public enum GenderEnum {
    Female, Male, Undefined;


    public static GenderEnum fromStringIgnoreCase(String value) {
        for (GenderEnum g : values()) {
            if (g.name().equalsIgnoreCase(value)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Only Female, Male, Undefined are valid");
    }
}