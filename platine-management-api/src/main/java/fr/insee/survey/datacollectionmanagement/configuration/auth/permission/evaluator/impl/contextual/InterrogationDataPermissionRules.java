package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterrogationDataPermissionRules {

    private final GlobalPermissionChecker globalRoleChecker;
    private final QuestioningAccreditationService questioningAccreditationService;
    private final QuestioningService questioningService;

    public boolean evaluate(
            Authentication authentication,
            UUID questioningId,
            Permission permission
    ) {
        boolean hasValidRole = globalRoleChecker.hasPermission(authentication, permission);
        ProfiledAuthenticationToken token = (ProfiledAuthenticationToken) authentication;
        String userId = token.getName().toUpperCase();

        if (!hasValidRole) {
            log.warn("Permission {} denied for questioning {}, user {} has no acceptable roles",
                    permission.name(), questioningId, userId);
            return false;
        }

        if (token.hasRole(AuthorityRoleEnum.ADMIN)) {
            log.info("Permission {} granted for questioning {}, user {} is admin",
                    permission.name(), questioningId, userId);
            return true;
        }

        if (questioningService.isValidatedInPaperEnvironment(questioningId)) {
            log.warn("Permission {} denied for questioning {}, user {} - questioning is validated in paper environment",
                    permission.name(), questioningId, userId);
            return false;
        }

        if (token.hasRole(AuthorityRoleEnum.RESPONDENT)) {
            boolean habilitated = questioningAccreditationService.hasAccreditation(questioningId, userId);
            if (habilitated) {
                log.info("Permission {} granted for questioning {}, user {} is respondent",
                        permission.name(), questioningId, userId);
            } else {
                log.warn("Permission {} denied for questioning {}, user {} is respondent but has no accreditation",
                        permission.name(), questioningId, userId);
            }
            return habilitated;
        }

        log.info("Permission {} granted for questioning {}, user {} passed global roles and is not respondent",
                permission.name(), questioningId, userId);
        return true;
    }
}
