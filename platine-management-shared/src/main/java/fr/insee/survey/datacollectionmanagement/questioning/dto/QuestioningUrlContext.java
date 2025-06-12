package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;

public record QuestioningUrlContext(String modelName,
                                    String surveyUnitId,
                                    Long questioningId,
                                    String campaignId,
                                    DataCollectionEnum dataCollection,
                                    String sourceId,
                                    Integer surveyYear,
                                    String period,
                                    String operationUpload,
                                    String contactId) {
}
