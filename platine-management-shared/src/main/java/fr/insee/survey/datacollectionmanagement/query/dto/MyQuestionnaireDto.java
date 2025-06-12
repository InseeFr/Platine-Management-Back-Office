package fr.insee.survey.datacollectionmanagement.query.dto;

import java.time.Instant;

public record MyQuestionnaireDto(
        String sourceId,
        String surveyUnitIdentificationCode,
        String surveyUnitIdentificationName,
        String questioningStatus,
        String questioningAccessUrl,
        String depositProofUrl,
        Long questioningId,
        String partitioningLabel,
        String partitioningId,
        Instant partitioningReturnDate,
        String surveyUnitId,
        String questioningDownloadFileName,
        String operationUploadReference
) { }
