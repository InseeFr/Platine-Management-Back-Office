package fr.insee.survey.datacollectionmanagement.configuration;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityRoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
public class AuthenticationUserProvider {

    public static JwtAuthenticationToken getAuthenticatedUser(String contactId, AuthorityRoleEnum... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (AuthorityRoleEnum role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.securityRole()));
        }

        Map<String, Object> headers = Map.of("typ", "JWT");
        Map<String, Object> claims = Map.of("preferred_username", contactId, "name", contactId);

        Jwt jwt = new Jwt("token-value", Instant.MIN, Instant.MAX, headers, claims);
        return new JwtAuthenticationToken(jwt, authorities, contactId);
    }

    public static AnonymousAuthenticationToken getNotAuthenticatedUser() {
        Map<String, String> principal = new HashMap<>();
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken("id", principal, List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        auth.setAuthenticated(false);
        return auth;
    }
}
