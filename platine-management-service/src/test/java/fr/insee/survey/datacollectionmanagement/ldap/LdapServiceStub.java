package fr.insee.survey.datacollectionmanagement.ldap;

import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.ldap.service.LdapService;

public class LdapServiceStub implements LdapService {
    @Override
    public ContactDto createUser(ContactDto contact) {
        contact.setIdentifier("Id");
        return  contact;
    }
}
