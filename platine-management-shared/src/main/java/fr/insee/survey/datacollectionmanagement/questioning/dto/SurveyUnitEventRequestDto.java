package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitEventSource;
import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SurveyUnitEventRequestDto(
    @NotBlank
    String campaignId,
    @NotNull
    SurveyUnitEventType eventType,
    @NotNull
    SurveyUnitEventSource source,
    @NotNull
    Long eventDate
){}
