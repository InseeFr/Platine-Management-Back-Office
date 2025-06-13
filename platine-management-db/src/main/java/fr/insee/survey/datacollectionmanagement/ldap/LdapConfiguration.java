package fr.insee.survey.datacollectionmanagement.ldap;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class LdapConfiguration {

    @Value("${fr.insee.datacollectionmanagement.ldap.api.url}")
    public String apiUrl;
    @Value("${fr.insee.datacollectionmanagement.ldap.api.pw}")
    public String serviceContactPassword;
    @Value("${fr.insee.datacollectionmanagement.ldap.api.login}")
    public String serviceContactLogin;

    @Bean
    public RestTemplate ldapRestTemplate() {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(apiUrl).build();
        restTemplate.getInterceptors()
                .add(new BasicAuthenticationInterceptor(serviceContactLogin, serviceContactPassword));

        return restTemplate;
    }
}

