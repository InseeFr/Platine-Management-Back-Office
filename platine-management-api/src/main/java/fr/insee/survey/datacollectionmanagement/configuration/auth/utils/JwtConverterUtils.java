package fr.insee.survey.datacollectionmanagement.configuration.auth.utils;

import fr.insee.survey.datacollectionmanagement.configuration.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class JwtConverterUtils {
    public static void fillGrantedRoles(List<String> configRoles, AuthorityRoleEnum authorityRole, Map<String, List<SimpleGrantedAuthority>>  roles) {
        if(configRoles == null) {
            return;
        }

        for (String configRole : configRoles ) {
            if(configRole == null || configRole.isBlank()) {
                return;
            }

            roles.compute(configRole, (key, grantedAuthorities) -> {
                if(grantedAuthorities == null) {
                    grantedAuthorities = new ArrayList<>();
                }
                grantedAuthorities.add(new SimpleGrantedAuthority(authorityRole.securityRole()));
                return grantedAuthorities;
            });
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getUserRoles(Jwt jwt, ApplicationConfig applicationConfig, String claim, String realmAccesRole) {
        Map<String, Object> claims = jwt.getClaims();

        if(applicationConfig.getRoleClaim() == null || applicationConfig.getRoleClaim().isBlank()) {
            Map<String, Object> realmAccess = jwt.getClaim(claim);
            return (List<String>) realmAccess.get(realmAccesRole);
        }
        return (List<String>) claims.get(applicationConfig.getRoleClaim());
    }
}