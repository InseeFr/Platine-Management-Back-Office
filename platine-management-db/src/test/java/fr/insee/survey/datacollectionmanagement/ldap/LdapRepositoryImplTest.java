package fr.insee.survey.datacollectionmanagement.ldap;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapAccreditationDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactOutputDto;
import fr.insee.survey.datacollectionmanagement.ldap.impl.LdapRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

class LdapServiceImplTest {

    LdapRepositoryImpl ldapRepository;

    private static final String REALM = "testRealm";
    private static final String STORAGE = "testStorage";
    private static final String ACCREDITATION_ID = "testId";
    private static final String ACCREDITATION_APP = "testApp";
    private static final String ACCREDITATION_ROLE = "testRole";
    private static final String ACCREDITATION_PROPERTY = "testProperty";
    private static final String PASSWORD = "pw";
    private static final String LOGIN = "login";

    public static final String PATH_SLASH = "/";
    public static final String REALMS_PATH = PATH_SLASH + "v2" + PATH_SLASH + "realms";
    public static final String STORAGES_PATH = PATH_SLASH + "storages";
    public static final String CONTACT_PATH = PATH_SLASH + "users";

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().dynamicHttpsPort())
            .build();


    public WebClient createTestWebCLient() {
        WireMockRuntimeInfo wmRuntimeInfo = wm.getRuntimeInfo();
        return WebClient.builder()
                .baseUrl(wmRuntimeInfo.getHttpBaseUrl())
                .filter(basicAuthentication(LOGIN, PASSWORD))
                .build();
    }

    public void createLdapTestProperties() {

        ReflectionTestUtils.setField(ldapRepository, "realm", REALM);
        ReflectionTestUtils.setField(ldapRepository, "storage", STORAGE);
        ReflectionTestUtils.setField(ldapRepository, "accreditationId", ACCREDITATION_ID);
        ReflectionTestUtils.setField(ldapRepository, "accreditationApplication", ACCREDITATION_APP);
        ReflectionTestUtils.setField(ldapRepository, "accreditationRole", ACCREDITATION_ROLE);
        ReflectionTestUtils.setField(ldapRepository, "accreditationProperty", ACCREDITATION_PROPERTY);
    }

    @BeforeEach
    void initServiceWithStubs() {
        ldapRepository = new LdapRepositoryImpl(createTestWebCLient());
        createLdapTestProperties();
    }

    String createResponseBody(String username)
    {
        return String.format("""
        {
            "username": "%s",
            "habilitations": [
                {
                    "id": "%s",
                    "application": "%s",
                    "role": "%s",
                    "property": "%s"
                }
            ]
        }
        """,
                username,
                ACCREDITATION_ID, ACCREDITATION_APP, ACCREDITATION_ROLE, ACCREDITATION_PROPERTY
        );
    }

    @Test
    @DisplayName("Should call API to create a user")
    void createUserInLdapAndReturnResponseEntity()
    {
        String path = REALMS_PATH + PATH_SLASH + REALM + STORAGES_PATH + PATH_SLASH + STORAGE + CONTACT_PATH;
        String username = "TESTID";
        wm.stubFor(post(path)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(createResponseBody(username))));


        ResponseEntity<LdapContactOutputDto> ldapContactOutputDtoResponseEntity = ldapRepository.createContact();
        assertThat(ldapContactOutputDtoResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(ldapContactOutputDtoResponseEntity.getBody()).getUsername()).isEqualTo("TESTID");

        List<LdapAccreditationDto> ldapAccreditationDtos = ldapContactOutputDtoResponseEntity.getBody().getHabilitations();

        assertThat(ldapAccreditationDtos).hasSize(1);
        assertThat(ldapAccreditationDtos.getFirst().getRole()).isEqualTo(ACCREDITATION_ROLE);
        assertThat(ldapAccreditationDtos.getFirst().getId()).isEqualTo(ACCREDITATION_ID);
        assertThat(ldapAccreditationDtos.getFirst().getProperty()).isEqualTo(ACCREDITATION_PROPERTY);
        assertThat(ldapAccreditationDtos.getFirst().getApplication()).isEqualTo(ACCREDITATION_APP);
        assertThat(ldapContactOutputDtoResponseEntity.getBody().getUsername()).isEqualTo(username);

    }
}
