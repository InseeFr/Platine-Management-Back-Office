package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import fr.insee.modelefiliere.CommunicationStepDto;
import fr.insee.modelefiliere.EntryBooleanValDto;
import fr.insee.modelefiliere.EntryStringValDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommunicationUtil {

    public static String findStringMetadataValue(CommunicationStepDto communication, String key) {
        return communication
                .getCommunicationModelMetadata()
                .stream()
                .filter(EntryStringValDto.class::isInstance)
                .map(EntryStringValDto.class::cast)
                .filter(entry -> entry.getKey().equals(key))
                .map(EntryStringValDto::getValue)
                .findFirst()
                .orElse(null);
    }

    public static Boolean findBooleanMetadataValue(CommunicationStepDto communication, String key) {
        return communication
                .getCommunicationModelMetadata()
                .stream()
                .filter(EntryBooleanValDto.class::isInstance)
                .map(EntryBooleanValDto.class::cast)
                .filter(entry -> entry.getKey().equals(key))
                .map(EntryBooleanValDto::getValue)
                .findFirst()
                .orElse(null);
    }

    public static Boolean findBooleanMetadataValue(List<CommunicationStepDto> communicationSteps,
                                                 CommunicationStepDto.CommunicationTypeEnum communicationType,
                                                 CommunicationStepDto.CommunicationMediumEnum communicationMedium,
                                                 String key) {

        return communicationSteps.stream()
                .filter(communicationStep ->
                        communicationType.equals(communicationStep.getCommunicationType())
                                && communicationMedium.equals(communicationStep.getCommunicationMedium())
                )
                .map(communicationStep -> findBooleanMetadataValue(communicationStep, key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public static String findStringMetadataValue(List<CommunicationStepDto> communicationSteps,
                                                 CommunicationStepDto.CommunicationTypeEnum communicationType,
                                                 CommunicationStepDto.CommunicationMediumEnum communicationMedium,
                                                 String key) {

        return communicationSteps.stream()
                .filter(communicationStep ->
                        communicationType.equals(communicationStep.getCommunicationType())
                                && communicationMedium.equals(communicationStep.getCommunicationMedium())
                )
                .map(communicationStep -> findStringMetadataValue(communicationStep, key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
