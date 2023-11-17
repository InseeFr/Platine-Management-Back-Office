package fr.insee.survey.datacollectionmanagement.config.auth.security;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
public class PublicSecurityFilterChain {

    @Autowired
    ApplicationConfig config;
    SecurityFilterChain buildSecurityPublicFilterChain(HttpSecurity http, String[] publicUrls) throws Exception {
        return buildSecurityPublicFilterChain(http, publicUrls, "");
    }

    SecurityFilterChain buildSecurityPublicFilterChain(HttpSecurity http, String[] publicUrls, String authorizedConnectionHost) throws Exception {
        return http
                .securityMatcher(publicUrls)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .xssProtection(xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                        .contentSecurityPolicy(cspConfig -> cspConfig
                                .policyDirectives("default-src 'none'; " +
                                        "connect-src 'self' " + authorizedConnectionHost + "; " +
                                        "img-src 'self' data:; " +
                                        "style-src 'self'; " +
                                        "script-src 'self' 'unsafe-inline'")
                        )
                        .referrerPolicy(referrerPolicy ->
                                referrerPolicy
                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                        ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers(publicUrls).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}
