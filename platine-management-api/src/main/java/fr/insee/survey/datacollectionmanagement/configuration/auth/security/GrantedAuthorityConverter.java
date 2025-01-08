package fr.insee.survey.datacollectionmanagement.configuration.auth.security;

import fr.insee.survey.datacollectionmanagement.configuration.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final Map<String, List<SimpleGrantedAuthority>> roles;
    ApplicationConfig applicationConfig;

    public GrantedAuthorityConverter(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.roles = new HashMap<>();
        fillGrantedRoles(applicationConfig.getRoleAdmin(), AuthorityRoleEnum.ADMIN);
        fillGrantedRoles(applicationConfig.getRoleRespondent(), AuthorityRoleEnum.RESPONDENT);
        fillGrantedRoles(applicationConfig.getRoleInternalUser(), AuthorityRoleEnum.INTERNAL_USER);
        fillGrantedRoles(applicationConfig.getRoleWebClient(), AuthorityRoleEnum.WEB_CLIENT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        List<String> userRoles = (List<String>) claims.get(applicationConfig.getRoleClaim());

        if(userRoles == null) {
            return new ArrayList<>();
        }

        return userRoles.stream()
                .filter(Objects::nonNull)
                .filter(role -> !role.isBlank())
                .filter(roles::containsKey)
                .map(roles::get)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void fillGrantedRoles(List<String> configRoles, AuthorityRoleEnum authorityRole) {

        for (String configRole : configRoles ) {
            if(configRole == null || configRole.isBlank()) {
                return;
            }

            this.roles.compute(configRole, (key, grantedAuthorities) -> {
                if(grantedAuthorities == null) {
                    grantedAuthorities = new ArrayList<>();
                }
                grantedAuthorities.add(new SimpleGrantedAuthority(authorityRole.securityRole()));
                return grantedAuthorities;
            });
        }
    }
}

