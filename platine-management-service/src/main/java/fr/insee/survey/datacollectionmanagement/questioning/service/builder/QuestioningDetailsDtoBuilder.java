package fr.insee.survey.datacollectionmanagement.questioning.service.builder;

import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentOutputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class QuestioningDetailsDtoBuilder {
    private final QuestioningDetailsDto instance;

    public QuestioningDetailsDtoBuilder() {
        instance = new QuestioningDetailsDto();
    }

    public QuestioningDetailsDtoBuilder isHousehold(Boolean isHousehold) {
        instance.setIsHousehold(isHousehold);
        return this;
    }

    public QuestioningDetailsDtoBuilder questioningId(Long id) {
    public QuestioningDetailsDtoBuilder questioningId(UUID id) {
        instance.setQuestioningId(id);
        return this;
    }

    public QuestioningDetailsDtoBuilder campaignId(String campaignId) {
        instance.setCampaignId(campaignId);
        return this;
    }

    public QuestioningDetailsDtoBuilder surveyUnit(QuestioningSurveyUnitDto surveyUnit) {
        if (surveyUnit != null) {
            instance.setSurveyUnitId(surveyUnit.idSu());
            instance.setSurveyUnitIdentificationCode(surveyUnit.identificationCode());
            instance.setSurveyUnitIdentificationName(surveyUnit.identificationName());
            instance.setSurveyUnitLabel(surveyUnit.label());
        }
        return this;
    }

    public QuestioningDetailsDtoBuilder contacts(List<QuestioningContactDto> contacts) {
        instance.setListContacts(contacts);
        return this;
    }

    public QuestioningDetailsDtoBuilder events(List<QuestioningEventDto> events, QuestioningEventDto lastEvent, QuestioningEventDto validatedEvent) {
        instance.setListEvents(events);
        if (lastEvent != null) {
            instance.setLastEvent(lastEvent.getType());
            instance.setDateLastEvent(lastEvent.getEventDate());
        }
        if (validatedEvent != null) {
            instance.setValidationDate(validatedEvent.getEventDate());
        }
        return this;
    }

    public QuestioningDetailsDtoBuilder communications(List<QuestioningCommunicationDto> communications) {
        instance.setListCommunications(communications);
        if (communications != null && !communications.isEmpty()) {
            QuestioningCommunicationDto lastCommunication = communications.stream()
                    .max(Comparator.comparing(QuestioningCommunicationDto::getDate))
                    .orElse(new QuestioningCommunicationDto());
            instance.setLastCommunication(lastCommunication.getType());
            instance.setDateLastCommunication(lastCommunication.getDate());
        }
        return this;
    }

    public QuestioningDetailsDtoBuilder comments(List<QuestioningCommentOutputDto> comments) {
        instance.setListComments(comments);
        return this;
    }

    public QuestioningDetailsDtoBuilder readOnlyUrl(String readOnlyUrl) {
        instance.setReadOnlyUrl(readOnlyUrl);
        return this;
    }

    public QuestioningDetailsDto build() {
        return instance;
    }
}
