package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.configuration.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ProfiledAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AuthorizationProfileFactory profileFactory;
    public static final String REALM_ACCESS_ROLE = "roles";
    public static final String REALM_ACCESS = "realm_access";
    private final Map<String, List<SimpleGrantedAuthority>> roles;
    private final ApplicationConfig applicationConfig;
    private final String principalClaimName;


    public ProfiledAuthenticationConverter(ApplicationConfig applicationConfig, AuthorizationProfileFactory profileFactory, String principalClaimName) {
        this.applicationConfig = applicationConfig;
        this.roles = new HashMap<>();
        this.profileFactory = profileFactory;
        this.principalClaimName = principalClaimName;
        fillGrantedRoles(applicationConfig.getRoleAdmin(), AuthorityRoleEnum.ADMIN);
        fillGrantedRoles(applicationConfig.getRoleRespondent(), AuthorityRoleEnum.RESPONDENT);
        fillGrantedRoles(applicationConfig.getRoleInternalUser(), AuthorityRoleEnum.INTERNAL_USER);
        fillGrantedRoles(applicationConfig.getRoleWebClient(), AuthorityRoleEnum.WEB_CLIENT);
        fillGrantedRoles(applicationConfig.getRolePortal(), AuthorityRoleEnum.PORTAL);
        fillGrantedRoles(applicationConfig.getRoleReader(), AuthorityRoleEnum.READER);
        fillGrantedRoles(applicationConfig.getRoleSupport(), AuthorityRoleEnum.SUPPORT);
    }



    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String name = jwt.getClaimAsString(principalClaimName);
        Set<AuthorityRoleEnum> applicationRoles = getApplicationRoles(jwt);
        Set<String> sources = new HashSet<>();
        AuthorizationProfile profile = profileFactory.buildProfile(applicationRoles, sources);

        Collection<GrantedAuthority> authorities = applicationRoles.stream()
                .map(authorityRoleEnum -> new SimpleGrantedAuthority(authorityRoleEnum.securityRole()))
                .collect(Collectors.toUnmodifiableList());


        return new ProfiledAuthenticationToken(jwt, authorities, name, profile);
    }

    private Set<AuthorityRoleEnum> getApplicationRoles(@NonNull Jwt jwt) {
        List<String> userRoles = getUserRoles(jwt);

        if(userRoles == null) {
            return new HashSet<>();
        }

        return userRoles.stream()
                .filter(Objects::nonNull)
                .filter(role -> !role.isBlank())
                .filter(roles::containsKey)
                .map(roles::get)
                .flatMap(Collection::stream)
                .distinct()
                .map(SimpleGrantedAuthority::getAuthority)
                .map(AuthorityRoleEnum::fromSecurityRole)
                .collect(Collectors.toUnmodifiableSet());
    }

    private void fillGrantedRoles(List<String> configRoles, AuthorityRoleEnum authorityRole) {

        if(configRoles == null) {
            return;
        }

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

    @SuppressWarnings("unchecked")
    private List<String> getUserRoles(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();

        if(applicationConfig.getRoleClaim() == null || applicationConfig.getRoleClaim().isBlank()) {
            Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
            return (List<String>) realmAccess.get(REALM_ACCESS_ROLE);
        }
        return (List<String>) claims.get(applicationConfig.getRoleClaim());
    }
}
