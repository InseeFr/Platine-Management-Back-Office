package fr.insee.survey.datacollectionmanagement.query.enums;

public enum QuestionaireStatusTypeEnum {
    RECEIVED("received"), NOT_RECEIVED("not received"), INCOMING("incoming"), OPEN("open");

    final String value;

    QuestionaireStatusTypeEnum(String type) {
        value = type;
    }

    public static QuestionaireStatusTypeEnum fromValue(String value) {
        for (QuestionaireStatusTypeEnum param : QuestionaireStatusTypeEnum.values()) {
            if (param.value.equalsIgnoreCase(value)) {
                return param;
            }
        }
        throw new IllegalArgumentException("No constant found for value: " + value);
    }
}
