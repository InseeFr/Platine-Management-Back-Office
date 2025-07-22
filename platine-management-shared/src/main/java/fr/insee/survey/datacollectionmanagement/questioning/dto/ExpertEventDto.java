package fr.insee.survey.datacollectionmanagement.questioning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;

public record ExpertEventDto(
        int score,
        @JsonProperty("score-init")
        int scoreInit,
        TypeExpertEvent type) {
    public enum TypeExpertEvent {
        EXPERT, ONGEXPERT, VALID, ENDEXPERT;

        public TypeQuestioningEvent toQuestioningEvent() {
            return TypeQuestioningEvent.valueOf(this.name());
        }
    }
}
