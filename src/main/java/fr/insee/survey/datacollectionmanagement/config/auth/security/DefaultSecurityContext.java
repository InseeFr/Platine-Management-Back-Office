package fr.insee.survey.datacollectionmanagement.config.auth.security;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthUser;
import fr.insee.survey.datacollectionmanagement.config.auth.user.UserProvider;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnMissingBean(OpenIDConnectSecurityContext.class)
@AllArgsConstructor
public class DefaultSecurityContext {

    private final PublicSecurityFilterChain publicSecurityFilterChainConfiguration;

    private final ApplicationConfig config;
    /**
     * Configure spring security filter chain when no authentication
     * @param http Http Security Object
     * @return the spring security filter
     * @throws Exception
     */
    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/**")
                .csrf(csrfConfig -> csrfConfig.disable())
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
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .build();
    }
    @Bean
    @Order(1)
    SecurityFilterChain filterPublicUrlsChain(HttpSecurity http) throws Exception {
        return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http, config.getPublicUrls());    }
    @Bean
    public UserProvider getUserProvider() {
        return auth -> new AuthUser("anonymous", Collections.emptyList());
    }


}