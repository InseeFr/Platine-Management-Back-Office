package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import fr.insee.modelefiliere.CommunicationStepDto;
import fr.insee.modelefiliere.ContextDto;
import fr.insee.modelefiliere.ContextMetadatasDto;
import fr.insee.modelefiliere.PartitionDto;

import java.util.Optional;

public record SurveyCreateContextDto(
        String id,
        Integer year,
        String longWording,
        String shortWording,
        String longObjectives,
        String visaNumber,
        String cnisUrl,
        String diffusionUrl,
        Boolean compulsoryNature,
        String rgpdBlock,
        String sendPaperQuestionnaire,
        String surveyStatus,
        Boolean sviUse,
        String sviNumber
) {

    public static SurveyCreateContextDto fromContextMetadatas(ContextDto context) {
        ContextMetadatasDto metadatas = context.getMetadatas();
        String longObjectives = null;
        String visaNumber = null;
        String cnisUrl = null;
        String diffusionUrl = null;
        Boolean compulsoryNature = null;
        String rgpdBlock = null;
        String sendPaperQuestionnaire = null;
        String surveyStatus = null;
        Boolean sviUse = null;
        String sviNumber = null;

        if(!context.getPartitions().isEmpty()) {
            PartitionDto firstPartition = context.getPartitions().getFirst();

            Optional<CommunicationStepDto> noticeAndLetterCommunicationOptional = firstPartition
                    .getCommunicationSteps()
                    .stream()
                    .filter(communicationStep -> CommunicationStepDto.CommunicationTypeEnum.NOTICE.equals(communicationStep.getCommunicationType()))
                    .filter(communicationStep -> CommunicationStepDto.CommunicationMediumEnum.LETTER.equals(communicationStep.getCommunicationMedium()))
                    .findFirst();

            if(noticeAndLetterCommunicationOptional.isPresent()) {
                CommunicationStepDto noticeAndLetterCommunication = noticeAndLetterCommunicationOptional.get();
                longObjectives = CommunicationUtil
                        .findStringMetadataValue(noticeAndLetterCommunication,
                                "longObjectives");
                visaNumber = CommunicationUtil
                        .findStringMetadataValue(noticeAndLetterCommunication,
                                "visaNumber");
                cnisUrl = CommunicationUtil
                        .findStringMetadataValue(noticeAndLetterCommunication,
                                "cnisUrl");
                diffusionUrl = CommunicationUtil
                        .findStringMetadataValue(noticeAndLetterCommunication,
                                "diffusionUrl");
                compulsoryNature =  Boolean.TRUE.equals(CommunicationUtil
                        .findBooleanMetadataValue(noticeAndLetterCommunication,
                                "compulsoryNature"));
                rgpdBlock = CommunicationUtil
                        .findStringMetadataValue(noticeAndLetterCommunication,
                                "rgpdInformation");
                sendPaperQuestionnaire = CommunicationUtil
                        .findStringMetadataValue(noticeAndLetterCommunication,
                                "questionnaireNumber");
                surveyStatus = CommunicationUtil
                        .findStringMetadataValue(noticeAndLetterCommunication,
                                "surveyStatus");
                sviUse =  Boolean.TRUE.equals(CommunicationUtil
                        .findBooleanMetadataValue(noticeAndLetterCommunication,
                                "svi"));
                sviNumber = CommunicationUtil
                        .findStringMetadataValue(noticeAndLetterCommunication,
                                "surveyCode");

            }
        }

        return new SurveyCreateContextDto(
                metadatas.getStatisticalOperationShortLabel(),      // id
                metadatas.getYear(),                                // year
                metadatas.getStatisticalOperationLabel(),           // longWording
                metadatas.getStatisticalOperationShortLabel(),      // shortWording
                longObjectives,                                     // longObjectives
                visaNumber,                                         // visaNumber
                cnisUrl,                                            // cnisUrl
                diffusionUrl,                                       // diffusionUrl
                compulsoryNature,                                   // compulsoryNature
                rgpdBlock,                                          // rgpdBlock
                sendPaperQuestionnaire,                             // sendPaperQuestionnaire
                surveyStatus,                                       // surveyStatus
                sviUse,                                             // sviUse
                sviNumber                                           // sviNumber
        );
    }
}