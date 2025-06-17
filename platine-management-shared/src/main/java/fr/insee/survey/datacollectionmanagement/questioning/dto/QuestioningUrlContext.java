package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;

import java.util.UUID;

public record QuestioningUrlContext(String modelName,
                                    String surveyUnitId,
                                    UUID questioningId,
                                    String campaignId,
                                    DataCollectionEnum dataCollection,
                                    String sourceId,
                                    Integer surveyYear,
                                    String period,
                                    String operationUpload,
                                    String contactId) {
}
