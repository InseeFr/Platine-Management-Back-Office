package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.NonNull;

import java.util.Set;

public record AuthorizationProfile(
        Set<AuthorityRoleEnum> appRoles,
        Set<String> sources,
        Set<Permission> permissions
) {

    public boolean can(@NonNull Permission permission, @NonNull String sourceId) {
        if(!this.permissions.contains(permission)) {
            return false;
        }

        if(sources == null) {
            return false;
        }
        return sources.contains(sourceId);
    }

    public static AuthorizationProfile emptyAuthorizationProfile() {
        return new AuthorizationProfile(null, null, null);
    }
}
