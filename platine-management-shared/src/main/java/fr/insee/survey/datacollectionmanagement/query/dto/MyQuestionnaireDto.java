package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.SourceTypeEnum;

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
        SourceTypeEnum sourceType
) { }
