package fr.insee.survey.datacollectionmanagement.questioning.enums;

import java.util.List;

public enum TypeQuestioningEvent {
    INITLA,
    FOLLOWUP,
    PND,
    WASTE,
    PARTIELINT,
    HC,
    VALPAP,
    VALINT,
    REFUSAL;

    public static final List<TypeQuestioningEvent> STATE_EVENTS = List.of(
            VALINT,
            VALPAP,
            REFUSAL,
            WASTE,
            HC,
            INITLA,
            PARTIELINT,
            PND);

    public static final List<TypeQuestioningEvent> FOLLOWUP_EVENTS = List.of(
            VALINT,
            VALPAP,
            REFUSAL,
            WASTE,
            HC);

    public static final List<TypeQuestioningEvent> EXTRACT_EVENTS = List.of(
            VALINT,
            PARTIELINT);

    public static final List<TypeQuestioningEvent> MY_QUESTIONINGS_EVENTS = List.of(
            PARTIELINT,
            HC,
            VALPAP,
            VALINT,
            REFUSAL);

    public static final List<TypeQuestioningEvent> VALIDATED_EVENTS = List.of(
            VALINT,
            VALPAP);

    public static final List<TypeQuestioningEvent> OPENED_EVENTS = List.of(
            INITLA,
            PARTIELINT);

    public static final List<TypeQuestioningEvent> REFUSED_EVENTS = List.of(
            WASTE,
            HC,
            REFUSAL);
}

