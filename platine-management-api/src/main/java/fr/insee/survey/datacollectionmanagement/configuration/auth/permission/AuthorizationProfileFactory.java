package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthorizationProfileFactory {

    public AuthorizationProfile buildProfile(Set<AuthorityRoleEnum> applicationRoles, Set<String> sources) {
        Set<Permission> permissions = Arrays.stream(Permission.values())
                .filter(Permission::global)
                .filter(permission ->
                        permission.isAllowedForRoles(applicationRoles)
                )
                .collect(Collectors.toSet());

        return new AuthorizationProfile(
                applicationRoles,
                sources,
                permissions
        );
    }
}


