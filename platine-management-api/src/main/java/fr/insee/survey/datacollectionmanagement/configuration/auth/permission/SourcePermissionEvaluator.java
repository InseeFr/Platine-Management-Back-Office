package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.strategy.SourceRetrievalStrategy;
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
     * Chech a permission with a target (ex. @PreAuthorize("hasPermission(#interroId, 'READ_SUPPORT')"))
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
        Permission permissionToCheck = Permission.valueOf((String) permission);

        if(!profile.permissions().contains(permissionToCheck)) {
            return false;
        }

        if(permissionToCheck.isGlobalPermission()) {
            return true;
        }

        SourceRetrievalStrategy sourceRetrievalStrategy = sourceStrategies.get(permissionToCheck);
        if (sourceRetrievalStrategy == null) {
            throw new SourceRetrievalStrategyException(targetDomainObject);
        }

        String sourceId = sourceRetrievalStrategy.getSourceId(targetDomainObject);
        return profile.can(sourceId);
    }

    /**
     * Check a permission with a target type (ex. @PreAuthorize("hasPermission(#interroId, 'READ', 'INTERROGATION')"))
     * not used at this moment but could be usefyl to separate permission from the resource we want to access
     * instead of 'READ_INTERRO' as permission, we could have 'READ' permission and 'INTERRO' as resource
     */
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId,
                                 String targetType,
                                 Object permission) {
        return hasPermission(authentication, targetId, permission);
    }
}
