package fr.insee.survey.datacollectionmanagement.ldap.service;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import org.springframework.stereotype.Service;

@Service
public interface LdapService {
    ContactDto createUser(ContactDto contact);
}
