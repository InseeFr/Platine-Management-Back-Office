package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AuthorizationProfileFactory {

    private static final Set<String> ALL_SOURCES = Set.of("1", "2", "3");

    public AuthorizationProfile buildProfile(Set<AuthorityRoleEnum> applicationRoles) {
        EnumMap<Permission, Set<String>> sourcesByPriviledge = new EnumMap<>(Permission.class);

        if(applicationRoles.contains(AuthorityRoleEnum.ADMIN)) {
            sourcesByPriviledge.put(Permission.READ_INTERRO, ALL_SOURCES);
            sourcesByPriviledge.put(Permission.UPDATE_INTERRO, ALL_SOURCES);
            sourcesByPriviledge.put(Permission.DELETE_INTERRO, ALL_SOURCES);
            return new AuthorizationProfile(applicationRoles, sourcesByPriviledge);
        }

        for(AuthorityRoleEnum applicationRole : applicationRoles) {
            switch(applicationRole) {
                case RESPONDENT -> sourcesByPriviledge.put(Permission.READ_INTERRO, Set.of("1"));
                case INTERNAL_USER -> {
                    sourcesByPriviledge.put(Permission.READ_INTERRO, Set.of("1"));
                    sourcesByPriviledge.put(Permission.UPDATE_INTERRO, Set.of("1"));
                    sourcesByPriviledge.put(Permission.DELETE_INTERRO, Set.of("1"));
                }
                // ...
                // don't forget to merge sources in previous permissions
                // for exemple the user has roles:
                // - internal user -> READ on 1
                // - respondent -> READ on 2
                // => READ -> [1,2]
                default -> throw new RuntimeException("ugly exception, should not happen as you implemented all the roles");
            }
        }
        return new AuthorizationProfile(applicationRoles, sourcesByPriviledge);
    }
}


