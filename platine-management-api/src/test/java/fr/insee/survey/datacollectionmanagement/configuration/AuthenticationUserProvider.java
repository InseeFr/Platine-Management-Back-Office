package fr.insee.survey.datacollectionmanagement.configuration;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.AuthorizationProfile;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.AuthorizationProfileFactory;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;


@AllArgsConstructor
public class AuthenticationUserProvider {

    public static ProfiledAuthenticationToken getAuthenticatedUser(String contactId, AuthorityRoleEnum... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (AuthorityRoleEnum role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.securityRole()));
        }

        Map<String, Object> headers = Map.of("typ", "JWT");
        Map<String, Object> claims = Map.of("preferred_username", contactId, "name", contactId);

        Jwt jwt = new Jwt("token-value", Instant.MIN, Instant.MAX, headers, claims);

        AuthorizationProfileFactory authorizationProfileFactory = new AuthorizationProfileFactory();
        HashSet<AuthorityRoleEnum> authorityRolesSet = new HashSet<>(Arrays.stream(roles).toList());
        AuthorizationProfile authorizationProfile = authorizationProfileFactory.buildProfile(authorityRolesSet, null);
        return new ProfiledAuthenticationToken(jwt, authorities, contactId, authorizationProfile);
    }

    public static AnonymousAuthenticationToken getNotAuthenticatedUser() {
        Map<String, String> principal = new HashMap<>();
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken("id", principal, List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        auth.setAuthenticated(false);
        return auth;
    }
}
