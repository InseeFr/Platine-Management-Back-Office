package fr.insee.survey.datacollectionmanagement.query.dto;

import java.time.Instant;
import java.util.UUID;

public record MyQuestionnaireDto(
        String sourceId,
        String surveyUnitIdentificationCode,
        String surveyUnitIdentificationName,
        String interrogationStatus,
        String interrogationAccessUrl,
        String depositProofUrl,
        UUID interrogationId,
        String partitioningLabel,
        String partitioningId,
        Instant partitioningReturnDate,
        String surveyUnitId,
        String interrogationDownloadFileName,
        String operationUploadReference,
        String sourceType
) { }
