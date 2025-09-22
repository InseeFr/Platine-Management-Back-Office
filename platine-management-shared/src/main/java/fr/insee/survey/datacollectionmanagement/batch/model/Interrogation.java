package fr.insee.survey.datacollectionmanagement.batch.model;

import fr.insee.modelefiliere.InterrogationDto;

import java.util.List;
import java.util.UUID;

public record Interrogation(
    UUID id,
    UUID surveyUnitId,
    UUID partitionId,
    Address address,
    String identificationName,
    String identificationCode,
    List<Contact> contacts
    ){

    public static Interrogation fromFiliereInterrogation(InterrogationDto dto) {
        return new Interrogation(
                dto.getInterrogationId(),
                dto.getSurveyUnitId(),
                dto.getPartitionId(),
                Address.fromFiliereAddress(dto.getAddress()),
                dto.getCorporateName(),
                dto.getDisplayName(),
                Contact.fromFiliereContacts(dto.getContacts())
        );
    }
}
