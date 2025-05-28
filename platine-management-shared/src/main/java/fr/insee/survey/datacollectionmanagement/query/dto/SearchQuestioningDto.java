package fr.insee.survey.datacollectionmanagement.query.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SearchQuestioningDto {
    UUID getQuestioningId();

    String getCampaignId();

    List<String> getListContactIdentifiers();

    String getSurveyUnitId();

    String getSurveyUnitIdentificationCode();

    String getLastEvent();

    String getLastCommunication();

    Date getValidationDate();
}
