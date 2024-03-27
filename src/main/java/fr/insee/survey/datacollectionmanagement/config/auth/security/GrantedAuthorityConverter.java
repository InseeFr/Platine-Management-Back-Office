package fr.insee.survey.datacollectionmanagement.config.auth.security;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    ApplicationConfig applicationConfig;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        List<String> roles = (List<String>) claims.get(applicationConfig.getRoleClaim());
        List<String> authorizedRoles = new ArrayList<>();
        authorizedRoles.addAll(applicationConfig.getRoleAdmin());
        authorizedRoles.addAll(applicationConfig.getRoleRespondent());
        authorizedRoles.addAll(applicationConfig.getRoleInternalUser());
        authorizedRoles.addAll(applicationConfig.getRoleWebClient());

        return roles.stream()
                .map(role -> {
                    if (authorizedRoles.contains(role)) {
                        return new SimpleGrantedAuthority(role);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

