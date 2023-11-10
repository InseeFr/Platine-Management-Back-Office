package fr.insee.survey.datacollectionmanagement.config.auth.security;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.config.auth.user.User;
import fr.insee.survey.datacollectionmanagement.config.auth.user.UserProvider;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "fr.insee.datacollectionmanagement.auth.mode", havingValue = "OIDC")
@Slf4j
@AllArgsConstructor
public class OpenIDConnectSecurityContext {


    private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;

    @Autowired
    ApplicationConfig config;

    @Bean
    @Order(2)
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .xssProtection(xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                        .contentSecurityPolicy(cspConfig -> cspConfig
                                .policyDirectives("default-src 'none'")
                        )
                        .referrerPolicy(referrerPolicy ->
                                referrerPolicy
                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                        ))
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, Constants.API_HEALTHCHECK).permitAll()
                        // actuator (actuator metrics are disabled by default)
                        .requestMatchers(HttpMethod.GET, Constants.ACTUATOR).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(config)))
                )
                .build();

    }


    @Bean
    @Order(1)
    SecurityFilterChain filterPublicUrlsChain(HttpSecurity http) throws Exception {
        String tokenUrl = config.getKeyCloakUrl() + "/realms/" + config.getKeycloakRealm() + "/protocol/openid-connect/token";
        String authorizedConnectionHost = config.getAuthType().equals("OIDC") ?
                " " + tokenUrl : "";
        return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http, publicUrls(), authorizedConnectionHost);
    }

    @Bean
    public UserProvider getUserProvider() {
        return auth -> {
            if ("anonymousUser".equals(auth.getPrincipal()))
                return null; //init request, or request without authentication
            final Jwt jwt = (Jwt) auth.getPrincipal();
            List<String> tryRoles = jwt.getClaimAsStringList(config.getRoleClaim());
            String tryId = jwt.getClaimAsString(config.getIdClaim());
            return new User(tryId, tryRoles);
        };
    }

    private String[] publicUrls() {
        return new String[]{"/csrf", "/", "/webjars/**", "/swagger-resources/**", "/environnement", Constants.API_HEALTHCHECK, "/actuator/**",
                "/swagger-ui/*", "/swagger-ui/html", "/v3/api-docs/swagger-config", "/v3/api-docs", "/openapi.json"};
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(ApplicationConfig applicationConfig) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter(applicationConfig));
        return jwtAuthenticationConverter;
    }

    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter(ApplicationConfig applicationConfig) {
        return new GrantedAuthorityConverter(applicationConfig);
    }

}
