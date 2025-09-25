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

        if(applicationRoles.contains(AuthorityRoleEnum.ADMIN)) {
            permissions.add(Permission.READ_INTERRO);
            permissions.add(Permission.UPDATE_INTERRO);
            permissions.add(Permission.DELETE_INTERRO);
            return new AuthorizationProfile(applicationRoles, sources, permissions);
        }

        for(AuthorityRoleEnum applicationRole : applicationRoles) {
            switch(applicationRole) {
                case RESPONDENT -> permissions.add(Permission.READ_INTERRO);
                case INTERNAL_USER -> {
                    permissions.add(Permission.READ_INTERRO);
                    permissions.add(Permission.UPDATE_INTERRO);
                    permissions.add(Permission.DELETE_INTERRO);
                }
                default -> throw new RuntimeException("ugly exception, should not happen as you implemented all the roles");
            }
        }
        return new AuthorizationProfile(applicationRoles, sources, permissions);
    }
}


