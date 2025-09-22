package fr.insee.survey.datacollectionmanagement.batch.model;

import fr.insee.modelefiliere.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;

import java.util.List;
import java.util.UUID;

public record Contact(
        UUID id,
        String identifier,
        GenderEnum gender,
        String firstName,
        String lastName,
        String function,
        String usualCompanyName,
        boolean isMain,
        String phoneNumber,
        String email
) {
    public static List<Contact> fromFiliereContacts(List<ContactDto> contacts) {
        return contacts.stream()
                .map(Contact::fromFiliereContact)
                .toList();
    }

    private static Contact fromFiliereContact(ContactDto dto) {
        return new Contact(
                dto.getContactId(),
                dto.getWebConnectionId(),
                GenderEnum.fromStringIgnoreCase(dto.getGender().getValue()),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getFunction(),
                dto.getBusinessName(),
                Integer.valueOf(1).equals(dto.getContactRank()),
                null,
                dto.getEmail()
        );
    }
}
