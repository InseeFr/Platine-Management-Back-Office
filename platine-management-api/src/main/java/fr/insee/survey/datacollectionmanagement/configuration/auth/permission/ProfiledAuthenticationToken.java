package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;


import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ProfiledAuthenticationToken extends JwtAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;
    private final transient AuthorizationProfile profile;

    public ProfiledAuthenticationToken(Jwt jwt,
                                       Collection<? extends GrantedAuthority> authorities,
                                       String name,
                                       AuthorizationProfile profile) {
        super(jwt, authorities, name);
        this.profile = profile;
    }

    public boolean hasRole(AuthorityRoleEnum role) {
        return this.profile.hasRole(role);
    }
}
