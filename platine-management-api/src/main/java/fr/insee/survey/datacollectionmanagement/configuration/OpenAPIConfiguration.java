package fr.insee.survey.datacollectionmanagement.configuration;


import fr.insee.survey.datacollectionmanagement.constants.AuthConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


@Configuration
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class OpenAPIConfiguration {

    private final BuildProperties buildProperties;

    private final ApplicationConfig applicationConfig;


    @Bean public OpenAPI customOpenAPI() {

        if (applicationConfig.getAuthType().equals(AuthConstants.OIDC)) {
            OAuthFlows flows = getoAuthFlows();

            return new OpenAPI()
                    .components(
                            new Components().addSecuritySchemes("oauth2", new SecurityScheme().type(SecurityScheme.Type.OAUTH2).flows(flows)))
                    .info(new Info().title(buildProperties.getName()).version(buildProperties.getVersion()))
                    .addSecurityItem(new SecurityRequirement().addList("oauth2", Arrays.asList("read", "write")));
        }
        return new OpenAPI()
                .info(new Info().title(buildProperties.getName()).version(buildProperties.getVersion()));

    }

    private OAuthFlows getoAuthFlows() {
        OAuthFlows flows = new OAuthFlows();
        OAuthFlow flow = new OAuthFlow();

        flow.setAuthorizationUrl(applicationConfig.getKeyCloakUrl() + "/realms/" + applicationConfig.getKeycloakRealm() + "/protocol/openid-connect/auth");
        flow.setTokenUrl(applicationConfig.getKeyCloakUrl() + "/realms/" + applicationConfig.getKeycloakRealm() + "/protocol/openid-connect/token");
        Scopes scopes = new Scopes();
        flow.setScopes(scopes);
        flows = flows.authorizationCode(flow);
        return flows;
    }


}

