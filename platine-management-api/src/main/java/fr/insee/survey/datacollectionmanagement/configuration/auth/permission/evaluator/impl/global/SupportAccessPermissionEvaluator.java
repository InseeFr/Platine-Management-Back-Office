package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.global;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.ApplicationPermissionEvaluator;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupportAccessPermissionEvaluator implements ApplicationPermissionEvaluator<Void> {

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
