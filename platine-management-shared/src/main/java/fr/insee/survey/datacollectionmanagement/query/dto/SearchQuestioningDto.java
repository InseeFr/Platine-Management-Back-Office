package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class SearchQuestioningDto {
    private final Long questioningId;
    private final String campaignId;
    private final TypeCommunicationEvent lastCommunicationType;
    private final Date validationDate;
    private final TypeQuestioningEvent highestEventType;
    private final String surveyUnitId;
    private final String identificationCode;
    private final List<String> contactIds;

    public SearchQuestioningDto(Long questioningId,
                                   String campaignId,
                                   TypeCommunicationEvent lastCommunicationType,
                                   Date validationDate,
                                   TypeQuestioningEvent highestEventType,
                                   String surveyUnitId,
                                   String identificationCode,
                                   String contactId) {
        this.questioningId = questioningId;
        this.campaignId = campaignId;
        this.lastCommunicationType = lastCommunicationType;
        this.validationDate = validationDate;
        this.highestEventType = highestEventType;
        this.surveyUnitId = surveyUnitId;
        this.identificationCode = identificationCode;
        this.contactIds = new ArrayList<>();
        this.contactIds.add(contactId);
    }

    public void addContactId(String contactId) {
        if(!this.contactIds.contains(contactId)) {
            this.contactIds.add(contactId);
        }
    }
}
