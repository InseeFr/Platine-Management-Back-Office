package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermissionEvaluatorHandler implements PermissionEvaluator {

    private final Map<Permission, ApplicationPermissionEvaluator<?>> permissionEvaluators;

    /**
     * Chech a permission with a target (ex. @PreAuthorize("hasPermission(#interroId, 'READ_SUPPORT')"))
     */
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject,
                                 Object permission) {

        if (permission == null) {
            return false;
        }

        if (!(permission instanceof String)) {
            return false;
        }

        Permission permissionToCheck = Permission.valueOf((String) permission);
        return hasPermission(authentication, targetDomainObject, permissionToCheck);
    }

    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject,
                                 Permission permissionToCheck) {

        if (permissionToCheck == null) {
            return false;
        }

        ApplicationPermissionEvaluator<?> permissionEvaluator = permissionEvaluators.get(permissionToCheck);
        if (permissionEvaluator == null) {
            throw new ApplicationPermissionEvaluatorException(targetDomainObject);
        }

        return invokeEvaluator(permissionEvaluator, authentication, targetDomainObject);
    }


    @SuppressWarnings("unchecked")
    private <T> boolean invokeEvaluator(
            ApplicationPermissionEvaluator<T> evaluator,
            Authentication authentication,
            Object target
    ) {
        if (target == null && evaluator.targetType() != Void.class) {
            throw new ApplicationPermissionEvaluatorException("Target required");
        }

        if (target != null && !evaluator.targetType().isAssignableFrom(target.getClass())) {
            throw new ApplicationPermissionEvaluatorException("Invalid target type");
        }

        return evaluator.hasPermission(authentication, (T) target);
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
