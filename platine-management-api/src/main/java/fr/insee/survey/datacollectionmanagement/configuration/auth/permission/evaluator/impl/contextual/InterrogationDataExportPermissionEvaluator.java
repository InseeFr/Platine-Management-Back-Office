package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.ApplicationPermissionEvaluator;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class InterrogationDataExportPermissionEvaluator implements ApplicationPermissionEvaluator<UUID> {

    private final GlobalPermissionChecker globalRoleChecker;
    private final QuestioningService questioningService;

    @Override
    public Permission permission() {
        return Permission.INTERROGATION_DATA_EXPORT;
    }

    @Override
    public Class<UUID> targetType() {
        return UUID.class;
    }

    @Override
    public boolean hasPermission(Authentication authentication, UUID questioningId) {
        boolean hasValidRole = globalRoleChecker.hasPermission(authentication, this.permission());
        ProfiledAuthenticationToken profiledAuthenticationToken = (ProfiledAuthenticationToken) authentication;
        String userId = profiledAuthenticationToken.getName().toUpperCase();

        if (!hasValidRole) {
            log.warn("Permission {} denied for questioning {}, user {} has no acceptable roles", this.permission().name(), questioningId, userId);
            return false;
        }

        boolean habilitated = questioningService.canExportQuestioningDataToPdf(questioningId);
        if(habilitated) {
            log.info("Permission {} granted for questioning {}, user {} is respondent", this.permission().name(), questioningId, userId);
        } else {
            log.warn("Permission {} denied for questioning {}, user {} is respondent but has no habilitation", this.permission().name(), questioningId, userId);
        }
        return habilitated;
    }
}
