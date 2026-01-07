package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.ApplicationPermissionEvaluator;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class InterrogationExpertDataEditPermissionEvaluator implements ApplicationPermissionEvaluator<UUID> {

    private final GlobalPermissionChecker globalRoleChecker;
    private final UserService userService;
    private final QuestioningService questioningService;

    @Override
    public Permission permission() {
        return Permission.INTERROGATION_EXPERT_DATA_EDIT;
    }

    @Override
    public Class<UUID> targetType() {
        return UUID.class;
    }

    @Override
    public boolean hasPermission(Authentication authentication, UUID questioningId) {
        boolean hasValidRole = globalRoleChecker.hasPermission(authentication, this.permission());
        if (!hasValidRole) {
            return false;
        }

        ProfiledAuthenticationToken profiledAuthenticationToken = (ProfiledAuthenticationToken) authentication;
        String userId = profiledAuthenticationToken.getName().toUpperCase();

        if(profiledAuthenticationToken.hasRole(AuthorityRoleEnum.ADMIN)) {
            log.info("Permission {} granted for questioning {}, admin user {}", this.permission().name(), questioningId, userId);
            return true;
        }

        Optional<User> optionalUser = userService.findOptionalByIdentifier(userId);
        if(optionalUser.isEmpty()) {
            log.warn("Permission {} denied for questioning {}, user {} does not exist", this.permission().name(), questioningId, userId);
            return false;
        }

        boolean questioningHasExpertiseStatus = questioningService.hasExpertiseStatus(questioningId);
        if(questioningHasExpertiseStatus) {
            log.info("Permission {} granted for questioning {}, internal user {}", this.permission().name(), questioningId, userId);
            return true;
        }

        log.warn("Permission {} denied for questioning {}, user {}", this.permission().name(), questioningId, userId);
        return false;
    }
}
