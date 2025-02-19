package fr.insee.survey.datacollectionmanagement.configuration.auth.security;

import fr.insee.survey.datacollectionmanagement.configuration.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthConstants;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "fr.insee.datacollectionmanagement.auth.mode", havingValue = AuthConstants.NOAUTH)
@AllArgsConstructor
public class NoAuthSecurityContext {

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
                .anonymous(anonymousConfig -> anonymousConfig
                        .authorities("ROLE_ADMIN"))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .build();
    }
    @Bean
    @Order(1)
    SecurityFilterChain filterPublicUrlsChain(HttpSecurity http) throws Exception {
        return publicSecurityFilterChainConfiguration.buildSecurityPublicFilterChain(http, config.getPublicUrls());
    }
}