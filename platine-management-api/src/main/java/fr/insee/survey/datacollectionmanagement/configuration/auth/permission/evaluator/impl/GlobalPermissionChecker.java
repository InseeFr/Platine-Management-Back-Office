package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.AuthorizationProfile;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class GlobalPermissionChecker {

    public boolean hasPermission(Authentication authentication, Permission permissionToCheck) {
        if (authentication == null) {
            return false;
        }

        if (!(authentication instanceof ProfiledAuthenticationToken)) {
            return false;
        }

        AuthorizationProfile profile = ((ProfiledAuthenticationToken) authentication).getProfile();
        return profile.permissions().contains(permissionToCheck);
    }
}
