package fr.insee.survey.datacollectionmanagement.ldap.service.impl;


import fr.insee.survey.datacollectionmanagement.ldap.LdapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("default")
class LdapServiceImplTest {

    LdapServiceImpl ldapService;
    LdapRepository ldapRepository;
    MockRestServiceServer server;

    @BeforeEach
    void initServiceWithStubs() {

        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();


        ldapRepository = new LdapRepository(restTemplate);
        ldapService = new LdapServiceImpl(ldapRepository);
    }

//    @Test
//    void test()
//    {
//        ldapService.createUser(new ContactDto());
//    }
}
