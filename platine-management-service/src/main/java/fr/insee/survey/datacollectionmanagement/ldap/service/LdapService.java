package fr.insee.survey.datacollectionmanagement.ldap.service;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;

public interface LdapService {
    ContactDto createUser(ContactDto contact);
}
