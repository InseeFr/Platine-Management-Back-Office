package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import org.springframework.security.core.Authentication;

public interface ApplicationPermissionEvaluator<T> {

    Permission permission();

    Class<T> targetType();

    boolean hasPermission(Authentication authentication, T targetDomainObject);
}
