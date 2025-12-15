package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.ApplicationPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupportPermissionEvaluator implements ApplicationPermissionEvaluator<Void> {

    private final GlobalPermissionChecker globalPermissionChecker;

    @Override
    public Permission permission() {
        return Permission.READ_SUPPORT;
    }

    @Override
    public Class<Void> targetType() {
        return Void.class;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Void unused) {
        return globalPermissionChecker.hasPermission(authentication, this.permission());
    }
}
