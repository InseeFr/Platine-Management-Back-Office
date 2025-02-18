package fr.insee.survey.datacollectionmanagement.questioning.enums;

import java.util.Arrays;
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

    public static final List<TypeQuestioningEvent> STATE_EVENTS = Arrays.asList(
            VALINT,
            VALPAP,
            REFUSAL,
            WASTE,
            HC,
            INITLA,
            PARTIELINT,
            PND);

    public static final List<TypeQuestioningEvent> FOLLOWUP_EVENTS = Arrays.asList(
            VALINT,
            VALPAP,
            REFUSAL,
            WASTE,
            HC);

    public static final List<TypeQuestioningEvent> EXTRACT_EVENTS = Arrays.asList(
            VALINT,
            PARTIELINT);

    public static final List<TypeQuestioningEvent> MY_QUESTIONINGS_EVENTS = Arrays.asList(
            PARTIELINT,
            HC,
            VALPAP,
            VALINT,
            REFUSAL);

    public static final List<TypeQuestioningEvent> VALIDATED_EVENTS = Arrays.asList(
            VALINT,
            VALPAP);
}

