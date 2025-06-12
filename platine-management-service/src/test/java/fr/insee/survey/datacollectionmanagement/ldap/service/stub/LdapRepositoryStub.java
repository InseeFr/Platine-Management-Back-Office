package fr.insee.survey.datacollectionmanagement.ldap.service.stub;

import fr.insee.survey.datacollectionmanagement.contact.dto.LdapAccreditationDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactOutputDto;
import fr.insee.survey.datacollectionmanagement.ldap.LdapRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class LdapRepositoryStub implements LdapRepository {

    public LdapAccreditationDto createTestLdapAccreditationDto()
    {
        LdapAccreditationDto ldapAccreditationDto = new LdapAccreditationDto();
        ldapAccreditationDto.setId("id");
        ldapAccreditationDto.setApplication("app");
        ldapAccreditationDto.setProperty("property");
        ldapAccreditationDto.setRole("role");
        return ldapAccreditationDto;
    }

    public LdapContactOutputDto createTestLdapContactOutputDto()
    {
        LdapContactOutputDto contact = new LdapContactOutputDto();
        contact.setUsername("TESTID");
        contact.setHabilitations(List.of(createTestLdapAccreditationDto()));
        contact.setUsername("TESTID");
        contact.setHabilitations(List.of(createTestLdapAccreditationDto()));
        return contact;
    }

    @Override
    public ResponseEntity<LdapContactOutputDto> createContact() {
        return new ResponseEntity<>(createTestLdapContactOutputDto(), HttpStatus.OK);
    }
}
