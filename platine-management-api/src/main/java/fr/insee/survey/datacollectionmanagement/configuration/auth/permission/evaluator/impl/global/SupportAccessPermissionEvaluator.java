package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.global;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.ApplicationPermissionEvaluator;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SupportAccessPermissionEvaluator implements ApplicationPermissionEvaluator<Void> {

    private final GlobalPermissionChecker globalPermissionChecker;

    @Override
    public Permission permission() {
        return Permission.SUPPORT_READ;
    }

    @Override
    public Class<Void> targetType() {
        return Void.class;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Void unused) {
        boolean hasValidRole = globalPermissionChecker.hasPermission(authentication, this.permission());
        String userId = authentication.getName().toUpperCase();

        if (hasValidRole) {
            log.info("Permission {} granted, user {}", this.permission().name(), userId);
        } else {
            log.warn("Permission {} denied, user {} has no acceptable roles", this.permission().name(), userId);
        }
        return hasValidRole;
    }
}
