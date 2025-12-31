package fr.insee.survey.datacollectionmanagement.questioning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.validation.IsExpertEvent;
import jakarta.validation.constraints.NotNull;

public record ExpertEventDto(
        int score,
        @JsonProperty("score-init")
        int scoreInit,
        @IsExpertEvent
        @NotNull
        TypeQuestioningEvent type,
        StatusEvent status) {
}
