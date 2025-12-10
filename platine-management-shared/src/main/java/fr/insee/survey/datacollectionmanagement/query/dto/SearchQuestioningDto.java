package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import lombok.Getter;

import java.util.*;

@Getter
public class SearchQuestioningDto {
    private final UUID questioningId;
    private final String campaignId;
    private final LastCommunicationDto lastCommunication;
    private final Date validationDate;
    private final TypeQuestioningEvent highestEventType;
    private final String surveyUnitId;
    private final String identificationCode;
    private final List<String> contactIds;
    private final Integer score;
    private final Long priority;

    public SearchQuestioningDto(UUID questioningId,
                                String campaignId,
                                LastCommunicationDto lastCommunication,
                                Date validationDate,
                                TypeQuestioningEvent highestEventType,
                                String surveyUnitId,
                                String identificationCode,
                                List<String> contactIds,
                                Integer score,
                                Long priority) {
        this.questioningId = questioningId;
        this.campaignId = campaignId;
        this.lastCommunication = lastCommunication;
        this.validationDate = validationDate;
        this.highestEventType = highestEventType;
        this.surveyUnitId = surveyUnitId;
        this.identificationCode = identificationCode;
        this.contactIds = contactIds;
        this.score = score;
        this.priority = priority;
    }

}
