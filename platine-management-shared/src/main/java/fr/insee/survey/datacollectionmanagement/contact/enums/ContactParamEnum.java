package fr.insee.survey.datacollectionmanagement.contact.enums;

import lombok.Getter;

@Getter
public enum ContactParamEnum {

    IDENTIFIER("identifier"), EMAIL("email"), NAME("name")  ;

    ContactParamEnum(String value) {
        this.value = value;
    }

    final String value;

    public static ContactParamEnum fromValue(String value) {
        for (ContactParamEnum param : ContactParamEnum.values()) {
            if (param.value.equalsIgnoreCase(value)) {
                return param;
            }
        }
        throw new IllegalArgumentException("No constant found for value: " + value);
    }
}
