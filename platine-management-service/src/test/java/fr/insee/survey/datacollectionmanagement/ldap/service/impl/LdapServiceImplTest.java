package fr.insee.survey.datacollectionmanagement.ldap.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactOutputDto;
import fr.insee.survey.datacollectionmanagement.ldap.service.stub.LdapRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LdapServiceImplTest {

    LdapServiceImpl ldapService;
    LdapRepositoryStub ldapRepository;

    @BeforeEach
    void initServiceWithStubs() {
        ldapRepository = new LdapRepositoryStub();
        ldapService = new LdapServiceImpl(ldapRepository);
    }

    @Test@DisplayName("Should create a contact and get its username from ldap")
    void createUserInLdap()
    {
        LdapContactOutputDto ldapContactOutputDto = ldapRepository.createTestLdapContactOutputDto();
        ContactDto contactDto = ldapService.createUser(new ContactDto());
        assertThat(contactDto.getIdentifier()).isEqualTo(ldapContactOutputDto.getUsername());
    }
}
