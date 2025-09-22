package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;


import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ProfiledAuthenticationToken extends JwtAuthenticationToken {

    private final AuthorizationProfile profile;

    public ProfiledAuthenticationToken(Jwt jwt,
                                       Collection<? extends GrantedAuthority> authorities,
                                       String name,
                                       AuthorizationProfile profile) {
        super(jwt, authorities, name);
        this.profile = profile;
    }

    public AuthorizationProfile getProfile() {
        return profile;
    }
}
