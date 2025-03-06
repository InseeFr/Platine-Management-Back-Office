package fr.insee.survey.datacollectionmanagement.query.enums;

public enum QuestionnaireStatusTypeEnum {
    RECEIVED("received"), NOT_RECEIVED("not received"), INCOMING("incoming"), OPEN("open");

    final String value;

    QuestionnaireStatusTypeEnum(String type) {
        value = type;
    }

    public static QuestionnaireStatusTypeEnum fromValue(String value) {
        for (QuestionnaireStatusTypeEnum param : QuestionnaireStatusTypeEnum.values()) {
            if (param.value.equalsIgnoreCase(value)) {
                return param;
            }
        }
        throw new IllegalArgumentException("No constant found for value: " + value);
    }
}
