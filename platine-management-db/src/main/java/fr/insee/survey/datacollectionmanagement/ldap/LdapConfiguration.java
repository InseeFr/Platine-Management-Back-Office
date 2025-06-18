package fr.insee.survey.datacollectionmanagement.ldap;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class LdapConfiguration {

    @Value("${fr.insee.datacollectionmanagement.ldap.api.url}")
    public String apiUrl;
    @Value("${fr.insee.datacollectionmanagement.ldap.api.pw}")
    public String serviceContactPassword;
    @Value("${fr.insee.datacollectionmanagement.ldap.api.login}")
    public String serviceContactLogin;

    @Bean
    public RestClient ldapWebClient() {
        ClientHttpRequestInterceptor clientHttpRequestInterceptor = new BasicAuthenticationInterceptor(serviceContactLogin, serviceContactPassword);
        return RestClient.builder()
                .baseUrl(apiUrl)
                .requestInterceptor(clientHttpRequestInterceptor)
                .build();
    }
}

