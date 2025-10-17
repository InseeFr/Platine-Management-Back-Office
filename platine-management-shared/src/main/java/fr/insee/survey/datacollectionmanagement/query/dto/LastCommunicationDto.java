package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import lombok.Getter;

@Getter
public class LastCommunicationDto {
    TypeCommunicationEvent typeCommunicationEvent;
    boolean withReceipt;
    boolean withQuestionnaire;

    public LastCommunicationDto(TypeCommunicationEvent typeCommunicationEvent, boolean withReceipt, boolean withQuestionnaire) {
        this.typeCommunicationEvent = typeCommunicationEvent;
        this.withReceipt = withReceipt;
        this.withQuestionnaire = withQuestionnaire;
    }
}
