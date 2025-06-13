package fr.insee.survey.datacollectionmanagement.ldap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LdapConfigurationTest {

    private LdapConfiguration ldapConfiguration;

    @BeforeEach
    public void setUp() {
        ldapConfiguration = new LdapConfiguration();
        ldapConfiguration.apiUrl = "https://example.com/ldap";
        ldapConfiguration.serviceContactLogin = "testUser";
        ldapConfiguration.serviceContactPassword = "testPass";
    }

    @Test
    void testLdapRestTemplate() {
        RestTemplate restTemplate = ldapConfiguration.ldapRestTemplate();

        assertThat(restTemplate).isNotNull();
        assertThat(restTemplate.getUriTemplateHandler().expand("/")).hasHost("example.com");

        List<?> interceptors = restTemplate.getInterceptors();
        assertThat(interceptors)
                .anyMatch(interceptor -> interceptor instanceof BasicAuthenticationInterceptor);

        BasicAuthenticationInterceptor authInterceptor = (BasicAuthenticationInterceptor) interceptors
                .stream()
                .filter(i -> i instanceof BasicAuthenticationInterceptor)
                .findFirst()
                .orElse(null);

        assertThat(authInterceptor).isNotNull();
    }
}
