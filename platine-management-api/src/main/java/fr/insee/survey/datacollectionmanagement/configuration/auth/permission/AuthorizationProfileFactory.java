package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AuthorizationProfileFactory {

    public AuthorizationProfile buildProfile(Set<AuthorityRoleEnum> applicationRoles, Set<String> sources) {
        Set<Permission> permissions = new HashSet<>();

        for(AuthorityRoleEnum applicationRole : applicationRoles) {
            Objects.requireNonNull(applicationRole);
            if (List.of(AuthorityRoleEnum.SUPPORT, AuthorityRoleEnum.ADMIN).contains(applicationRole)) {
                permissions.add(Permission.READ_SUPPORT);
            }

            if (List.of(AuthorityRoleEnum.INTERNAL_USER, AuthorityRoleEnum.ADMIN).contains(applicationRole)) {
                permissions.add(Permission.READ_PDF_RESPONSE);
            }
        }
        return new AuthorizationProfile(applicationRoles, sources, permissions);
    }
}


