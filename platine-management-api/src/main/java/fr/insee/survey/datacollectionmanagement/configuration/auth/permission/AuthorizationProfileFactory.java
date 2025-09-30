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
            if (!Objects.requireNonNull(applicationRole).equals(AuthorityRoleEnum.SUPPORT)) {
                permissions.add(Permission.READ_AND_WRITE);
            }
        }
        return new AuthorizationProfile(applicationRoles, sources, permissions);
    }
}


