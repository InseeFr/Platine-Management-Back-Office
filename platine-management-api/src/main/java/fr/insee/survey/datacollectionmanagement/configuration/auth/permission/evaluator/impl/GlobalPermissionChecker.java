package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class GlobalPermissionChecker {

    public boolean hasPermission(Authentication authentication, Permission permission) {
        if (authentication == null) {
            return false;
        }

        if (!(authentication instanceof ProfiledAuthenticationToken profiledAuthenticationToken)) {
            return false;
        }
        return permission.isAllowedForRoles(profiledAuthenticationToken.getProfile().appRoles());
    }
}
