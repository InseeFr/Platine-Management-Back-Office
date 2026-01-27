package fr.insee.survey.datacollectionmanagement.integration;


import io.cucumber.spring.ScenarioScope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@ScenarioScope
@Component
public class TestSecurityContext {
    private Authentication authentication;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
