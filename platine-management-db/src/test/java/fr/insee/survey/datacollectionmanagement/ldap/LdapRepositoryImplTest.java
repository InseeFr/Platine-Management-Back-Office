package fr.insee.survey.datacollectionmanagement.ldap;

import com.github.tomakehurst.wiremock.WireMockServer;
import fr.insee.survey.datacollectionmanagement.configuration.LdapApiProperties;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapAccreditationDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactOutputDto;
import fr.insee.survey.datacollectionmanagement.ldap.impl.LdapRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class LdapRepositoryImplTest {

    LdapRepositoryImpl ldapRepository;

    private WireMockServer wireMockServer;

    private final LdapApiProperties properties = new LdapApiProperties("testRealm",
            "testStorage",
            "testApp",
            "testRole");

    public static final String PATH_SLASH = "/";
    public static final String REALMS_PATH = PATH_SLASH + "v2" + PATH_SLASH + "realms";
    public static final String STORAGES_PATH = PATH_SLASH + "storages";
    public static final String CONTACT_PATH = PATH_SLASH + "users";

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();

        ClientHttpRequestInterceptor clientHttpRequestInterceptor = new BasicAuthenticationInterceptor("login", "pwd");
        RestClient restClient = RestClient.builder()
                .baseUrl(wireMockServer.baseUrl())
                .requestInterceptor(clientHttpRequestInterceptor)
                .build();

        ldapRepository = new LdapRepositoryImpl(restClient, properties);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    String createResponseBody(String username)
    {
        return String.format("""
        {
            "username": "%s",
            "habilitations": [
                {
                    "application": "%s",
                    "role": "%s"
                }
            ]
        }
        """,
                username,
                properties.accreditationApplication(), properties.accreditationRole()
        );
    }

    @Test
    @DisplayName("Should call API to create a user")
    void createUserInLdapAndReturnResponseEntity()
    {
        String path = REALMS_PATH + PATH_SLASH + properties.realm() +
                STORAGES_PATH + PATH_SLASH + properties.storage() + CONTACT_PATH;
        String username = "TESTID";
        wireMockServer.stubFor(post(path)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createResponseBody(username))));


        ResponseEntity<LdapContactOutputDto> ldapContactOutputDtoResponseEntity = ldapRepository.createContact();
        assertThat(ldapContactOutputDtoResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(ldapContactOutputDtoResponseEntity.getBody()).getUsername()).isEqualTo("TESTID");

        List<LdapAccreditationDto> ldapAccreditationDtos = ldapContactOutputDtoResponseEntity.getBody().getHabilitations();

        assertThat(ldapAccreditationDtos).hasSize(1);
        assertThat(ldapAccreditationDtos.getFirst().getRole()).isEqualTo(properties.accreditationRole());
        assertThat(ldapAccreditationDtos.getFirst().getApplication()).isEqualTo(properties.accreditationApplication());
        assertThat(ldapContactOutputDtoResponseEntity.getBody().getUsername()).isEqualTo(username);

    }
}
