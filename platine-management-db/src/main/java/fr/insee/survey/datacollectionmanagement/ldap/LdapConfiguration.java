package fr.insee.survey.datacollectionmanagement.ldap;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Configuration
public class LdapConfiguration {

    @Value("${fr.insee.datacollectionmanagement.ldap.api.url}")
    public String apiUrl;
    @Value("${fr.insee.datacollectionmanagement.ldap.api.pw}")
    public String serviceContactPassword;
    @Value("${fr.insee.datacollectionmanagement.ldap.api.login}")
    public String serviceContactLogin;

    @Bean
    public WebClient ldapWebClient() {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .filter(basicAuthentication(serviceContactLogin, serviceContactPassword))
                .build();
    }
}

