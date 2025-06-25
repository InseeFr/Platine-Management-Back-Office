package fr.insee.survey.datacollectionmanagement.query.dto;

import java.time.Instant;
import java.util.UUID;

public record MyQuestionnaireDto(
        String sourceId,
        String surveyUnitIdentificationCode,
        String surveyUnitIdentificationName,
        String questioningStatus,
        String questioningAccessUrl,
        String depositProofUrl,
        UUID questioningId,
        String partitioningLabel,
        String partitioningId,
        Instant partitioningReturnDate,
        String surveyUnitId,
        String questioningDownloadFileName,
        String operationUploadReference
) { }
