package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import fr.insee.modelefiliere.CommunicationStepDto;
import fr.insee.modelefiliere.ContextDto;
import fr.insee.modelefiliere.PartitionDto;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.*;

public record PartitioningCreateContextDto(
        @NotBlank String id,
        UUID technicalId,
        String label,
        Date openingDate,
        Date closingDate,
        Date returnDate,
        Date openingLetterDate,
        Date openingMailDate,
        Date followupLetter1Date,
        Date followupLetter2Date,
        Date followupLetter3Date,
        Date followupLetter4Date,
        Date followupMail1Date,
        Date followupMail2Date,
        Date followupMail3Date,
        Date followupMail4Date,
        Date formalNoticeDate,
        Date noReplyDate
) {

    public static PartitioningCreateContextDto fromPartitionDto(PartitionDto partition) {
        List<CommunicationStepDto> orderedCommunicationSteps = partition.getCommunicationSteps().stream()
                .sorted(Comparator.comparing(CommunicationStepDto::getCommunicationDate))
                .toList();

        Date openingLetterDate = findDateFromCommunicationSteps(orderedCommunicationSteps,
                CommunicationStepDto.CommunicationTypeEnum.NOTICE,
                CommunicationStepDto.CommunicationMediumEnum.LETTER);
        Date openingMailDate = findDateFromCommunicationSteps(orderedCommunicationSteps,
                CommunicationStepDto.CommunicationTypeEnum.NOTICE,
                CommunicationStepDto.CommunicationMediumEnum.EMAIL);
        Date formalNoticeDate = findDateFromCommunicationSteps(orderedCommunicationSteps,
                CommunicationStepDto.CommunicationTypeEnum.FORMAL_NOTICE,
                CommunicationStepDto.CommunicationMediumEnum.LETTER);

        Date noReplyDate = findDateFromCommunicationSteps(orderedCommunicationSteps,
                CommunicationStepDto.CommunicationTypeEnum.NON_RESPONSE,
                CommunicationStepDto.CommunicationMediumEnum.LETTER);

        List<Date> followUpLetterDates = findFollowUpDatesFromCommunicationSteps(orderedCommunicationSteps,
                CommunicationStepDto.CommunicationTypeEnum.REMINDER,
                CommunicationStepDto.CommunicationMediumEnum.LETTER);

        List<Date> followUpMailDates = findFollowUpDatesFromCommunicationSteps(orderedCommunicationSteps,
                CommunicationStepDto.CommunicationTypeEnum.REMINDER,
                CommunicationStepDto.CommunicationMediumEnum.EMAIL);

        return new PartitioningCreateContextDto(
                partition.getPartitionShortLabel(),                // id
                partition.getPartitionId(),                        // technicalId
                partition.getPartitionLabel(),                     // label
                toDate(partition.getCollectionStartDate()),        // openingDate
                toDate(partition.getCollectionEndDate()),          // closingDate
                toDate(partition.getReturnDate()),                 // returnDate
                openingLetterDate,                                    // openingLetterDate
                openingMailDate,                                      // openingMailDate
                followUpLetterDates.getFirst(),                       // followupLetter1Date
                followUpLetterDates.get(1),                           // followupLetter2Date
                followUpLetterDates.get(2),                           // followupLetter3Date
                followUpLetterDates.get(3),                           // followupLetter4Date
                followUpMailDates.getFirst(),                         // followupMail1Date
                followUpMailDates.get(1),                             // followupMail2Date
                followUpMailDates.get(2),                             // followupMail3Date
                followUpMailDates.get(3),                             // followupMail4Date
                formalNoticeDate,                                     // formalNoticeDate
                noReplyDate                                           // noReplyDate
        );
    }

    private static List<Date> findFollowUpDatesFromCommunicationSteps(List<CommunicationStepDto> communicationSteps,
                                                       CommunicationStepDto.CommunicationTypeEnum communicationType,
                                                       CommunicationStepDto.CommunicationMediumEnum communicationMedium) {
        List<Date> followUpDates = new ArrayList<>(communicationSteps.stream()
                .filter(communicationStep -> communicationType.equals(communicationStep.getCommunicationType()))
                .filter(communicationStep -> communicationMedium.equals(communicationStep.getCommunicationMedium()))
                .map(CommunicationStepDto::getCommunicationDate)
                .map(PartitioningCreateContextDto::toDate)
                .toList());


        if (followUpDates.size() < 4) {
            followUpDates.addAll(Collections.nCopies(4 - followUpDates.size(), null));
        }
        return followUpDates;
    }

    private static Date findDateFromCommunicationSteps(List<CommunicationStepDto> communicationSteps,
                                                        CommunicationStepDto.CommunicationTypeEnum communicationType,
                                                        CommunicationStepDto.CommunicationMediumEnum communicationMedium) {
        return communicationSteps.stream()
                .filter(communicationStep -> communicationType.equals(communicationStep.getCommunicationType()))
                .filter(communicationStep -> communicationMedium.equals(communicationStep.getCommunicationMedium()))
                .map(CommunicationStepDto::getCommunicationDate)
                .findFirst()
                .map(PartitioningCreateContextDto::toDate)
                .orElse(null);
    }

    private static Date toDate(Instant instant) {
        return instant != null ? Date.from(instant) : null;
    }
}