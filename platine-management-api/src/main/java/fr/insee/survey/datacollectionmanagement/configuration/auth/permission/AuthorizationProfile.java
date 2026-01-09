package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.NonNull;

import java.util.Set;

public record AuthorizationProfile(
        Set<AuthorityRoleEnum> appRoles,
        Set<String> sources,
        Set<Permission> permissions
) {

    public boolean can(@NonNull String sourceId) {
        if(sources == null) {
            return false;
        }
        return sources.contains(sourceId);
    }

    public boolean hasRole(AuthorityRoleEnum role) {
        if(appRoles == null) {
            return false;
        }
        return appRoles.contains(role);
    }

    public static AuthorizationProfile emptyAuthorizationProfile() {
        return new AuthorizationProfile(null, null, null);
    }
}
