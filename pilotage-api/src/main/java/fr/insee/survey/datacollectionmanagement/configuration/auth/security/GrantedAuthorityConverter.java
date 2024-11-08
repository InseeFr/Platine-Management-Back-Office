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

    private final Map<String, SimpleGrantedAuthority> grantedRoles;
    ApplicationConfig applicationConfig;

    public GrantedAuthorityConverter(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.grantedRoles = new HashMap<>();
        fillGrantedRoles(applicationConfig.getRoleAdmin(), AuthorityRoleEnum.ADMIN);
        fillGrantedRoles(applicationConfig.getRoleRespondent(), AuthorityRoleEnum.RESPONDENT);
        fillGrantedRoles(applicationConfig.getRoleInternalUser(), AuthorityRoleEnum.INTERNAL_USER);
        fillGrantedRoles(applicationConfig.getRoleWebClient(), AuthorityRoleEnum.WEB_CLIENT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        List<String> roles = (List<String>) claims.get(applicationConfig.getRoleClaim());

        return roles.stream()
                .filter(Objects::nonNull)
                .filter(role -> !role.isBlank())
                .filter(grantedRoles::containsKey)
                .map(grantedRoles::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void fillGrantedRoles(List<String> listRoles, AuthorityRoleEnum roleEnum) {

        for (String role : listRoles ) {
            this.grantedRoles.putIfAbsent(role,
                    new SimpleGrantedAuthority(roleEnum.securityRole()));
        }

    }
}

