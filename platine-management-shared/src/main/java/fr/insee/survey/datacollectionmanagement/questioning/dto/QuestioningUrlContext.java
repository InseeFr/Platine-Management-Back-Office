package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;

import java.util.UUID;

public record QuestioningUrlContext(String surveyUnitId,
                                    UUID questioningId,
                                    boolean isBusiness,
                                    String surveyUnitLabel,
                                    String surveyUnitIdentificationName,
                                    String campaignId,
                                    DataCollectionEnum dataCollection,
                                    String sourceId,
                                    Integer surveyYear,
                                    String period,
                                    String operationUpload,
                                    String contactId) {
}
