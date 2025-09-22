package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.strategy.SourceRetrievalStrategy;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SourcePermissionEvaluator implements PermissionEvaluator {

    private final Map<Permission, SourceRetrievalStrategy> sourceStrategies;

    /**
     * Chech a permission with a target (ex. @PreAuthorize("hasPermission(#interroId, 'READ_INTERRO')"))
     */
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject,
                                 Object permission) {

        if (authentication == null || permission == null) {
            return false;
        }

        if(!(permission instanceof String)) {
            return false;
        }

        if (!(authentication instanceof ProfiledAuthenticationToken)) {
            return false;
        }

        AuthorizationProfile profile = ((ProfiledAuthenticationToken) authentication).getProfile();
        if(profile.appRoles().contains(AuthorityRoleEnum.ADMIN)) {
            return true;
        }

        Permission permissionEnum = Permission.valueOf((String) permission);

        if(!profile.permissions().contains(permissionEnum)) {
            return false;
        }

        SourceRetrievalStrategy sourceRetrievalStrategy = sourceStrategies.get(permissionEnum);
        if (sourceRetrievalStrategy == null) {
            // TODO: custom exception, no strategy found
            throw new RuntimeException("permission strategy not found");
        }

        String sourceId = sourceRetrievalStrategy.getSourceId(targetDomainObject);

        return profile.can(permissionEnum, sourceId);
    }

    /**
     * Chech a permission with a target type (ex. @PreAuthorize("hasPermission(#interroId, 'READ', 'INTERROGATION')"))
     * not used at this moment but could be usefyl to separate permission from the resource we want to access
     * instead of 'READ_INTERRO' as permission, we could have 'READ' permission and 'INTERRO' as resource
     */
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId,
                                 String targetType,
                                 Object permission) {
        // redirige vers l'autre méthode
        return hasPermission(authentication, targetId, permission);
    }
}
