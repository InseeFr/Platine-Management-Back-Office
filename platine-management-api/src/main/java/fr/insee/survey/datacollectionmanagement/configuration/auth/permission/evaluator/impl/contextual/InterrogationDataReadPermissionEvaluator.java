package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.ApplicationPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class InterrogationDataReadPermissionEvaluator implements ApplicationPermissionEvaluator<UUID> {

    private final InterrogationDataPermissionRules rules;

    @Override
    public Permission permission() {
        return Permission.INTERROGATION_DATA_READ;
    }

    @Override
    public Class<UUID> targetType() {
        return UUID.class;
    }

    @Override
    public boolean hasPermission(Authentication authentication, UUID questioningId) {
        return rules.evaluate(authentication, questioningId, permission());
    }
}
