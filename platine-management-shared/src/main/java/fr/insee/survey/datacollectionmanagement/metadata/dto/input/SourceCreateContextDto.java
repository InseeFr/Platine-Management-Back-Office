package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import fr.insee.modelefiliere.*;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;

import java.util.Optional;

public record SourceCreateContextDto(
        String id,
        String type,
        String shortWording,
        String longWording,
        PeriodicityEnum periodicity,
        boolean personalData
) {

    public static SourceCreateContextDto fromContextMetadatas(ContextDto context) {
        ContextMetadatasDto metadatas = context.getMetadatas();
        boolean personalData = false;
        if(!context.getPartitions().isEmpty()) {
            PartitionDto firstPartition = context.getPartitions().getFirst();

            personalData = Boolean.TRUE.equals(CommunicationUtil
                    .findBooleanMetadataValue(firstPartition.getCommunicationSteps(),
                            CommunicationStepDto.CommunicationTypeEnum.NOTICE,
                            CommunicationStepDto.CommunicationMediumEnum.LETTER,
                            "personalDataParagraph"));
        }

        return new SourceCreateContextDto(
                metadatas.getStatisticalOperationSerieShortLabel(),
                context.getContext().toString(),
                metadatas.getStatisticalOperationSerieShortLabel(),
                metadatas.getStatisticalOperationSerieLabel(),
                PeriodicityEnum.valueOf(metadatas.getPeriodicity().name()),
                personalData
                );
    }
}

