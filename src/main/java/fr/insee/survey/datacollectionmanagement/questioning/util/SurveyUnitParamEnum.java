package fr.insee.survey.datacollectionmanagement.questioning.util;

import lombok.Getter;

@Getter
public enum SurveyUnitParamEnum {

    IDENTIFIER("id"), CODE("code"), NAME("name") ;

    SurveyUnitParamEnum(String value) {
        this.value = value;
    }

    final String value;

    public static SurveyUnitParamEnum fromValue(String value) {
        for (SurveyUnitParamEnum param : SurveyUnitParamEnum.values()) {
            if (param.value.equalsIgnoreCase(value)) {
                return param;
            }
        }
        throw new IllegalArgumentException("No constant found for value: " + value);
    }
}
