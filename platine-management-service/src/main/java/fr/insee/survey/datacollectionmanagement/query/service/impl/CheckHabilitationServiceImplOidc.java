package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.AuthConstants;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.enums.UserRoleTypeEnum;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "fr.insee.datacollectionmanagement.auth.mode", havingValue = AuthConstants.OIDC)
@Slf4j
public class CheckHabilitationServiceImplOidc implements CheckHabilitationService {

    private final ViewService viewService;

    private final UserService userService;

    private final QuestioningAccreditationService questioningAccreditationService;
    private final QuestioningService questioningService;

    @Override
    public boolean checkHabilitation(String role,
                                     String idSu,
                                     String campaignId,
                                     List<String> userRoles,
                                     String userId) {

        //admin
        if (isAdmin(userRoles)) {
            log.info("Check habilitation of admin {} for accessing survey-unit {} of campaign {} resulted in true", userId, idSu, campaignId);
            return true;
        }

        //respondents
        if (StringUtils.isBlank(role) || role.equals(UserRoles.INTERVIEWER)){
            if (isRespondent(userRoles)) {
                boolean habilitated = viewService.countViewByIdentifierIdSuCampaignId(userId.toUpperCase(), idSu, campaignId) != 0;
                log.info("Check habilitation of interviewer {} for accessing survey-unit {} of campaign {} resulted in {}", userId, idSu, campaignId, habilitated);
                return habilitated;
            }
            log.warn("Check habilitation of interviewer {} for accessing survey-unit {} of campaign {} - no respondent habilitation found in token - check habilitation: false", userId, idSu, campaignId);
            return false;
        }

        return checkInternal(role, userRoles, userId);
    }

    @Override
    public boolean checkHabilitation(String role, UUID questioningId, List<String> userRoles, String userId) {
        //admin
        if (isAdmin(userRoles)) {
            log.info("Check habilitation of admin {} for accessing questioning {} resulted in true", userId, questioningId);
            return true;
        }

        // check habilitation for respondent
        if (StringUtils.isBlank(role) || role.equals(UserRoles.INTERVIEWER)){
            if (isRespondent(userRoles)) {
                boolean habilitated = questioningAccreditationService.hasAccreditation(questioningId, userId);
                log.info("Check habilitation of interviewer {} for accessing questioning {} resulted in {}", userId, questioningId, habilitated);
                return habilitated;
            }
            log.warn("Check habilitation of interviewer {} for accessing questioning {} - no respondent habilitation found in token - check habilitation: false", userId, questioningId);
            return false;
        }

        if (UserRoles.EXPERT.equals(role) && isInternalUser(userRoles)) {
            return checkExpertise(userId, questioningId);
        }

        return checkInternal(role, userRoles, userId);
    }

    private boolean checkExpertise(String userId, UUID questioningId) {
        Optional<User> optionalUser = userService.findOptionalByIdentifier(userId);
        if(optionalUser.isEmpty()) {
            log.warn("User '{}' doesn't exists", userId);
            return false;
        }

        return questioningService.hasExpertiseStatut(questioningId);
    }

    private boolean checkInternal(String role, List<String> userRoles, String userId) {
        // internal users
        if (!UserRoles.REVIEWER.equals(role)) {
            log.warn("User {} - internal user habilitation not found in token - Check habilitation:false", userId);
            return false;
        }

        Optional<User> optionalUser = userService.findOptionalByIdentifier(userId);
        if(optionalUser.isEmpty()) {
            log.warn("User '{}' doesn't exists", userId);
            return false;
        }

        if (isInternalUser(userRoles)) {
            UserRoleTypeEnum userRole = optionalUser.get().getRole();
            if (userRole == UserRoleTypeEnum.ASSISTANCE) {
                log.warn("User '{}' has assistance profile - check habilitation: false", userId);
                return false;
            }
            log.warn("User '{}' has {} profile - check habilitation: true", userId, userRole);
            return true;
        }
        log.warn("Only '{}' and '{}' are accepted as a role in query argument", UserRoles.REVIEWER, UserRoles.INTERVIEWER);
        return false;
    }

    private boolean isAdmin(List<String> roles) {
        return roles.contains(AuthorityRoleEnum.ADMIN.securityRole());
    }

    private boolean isRespondent(List<String> roles) {
        return roles.contains(AuthorityRoleEnum.RESPONDENT.securityRole());
    }

    private boolean isInternalUser(List<String> roles) {
        return roles.contains(AuthorityRoleEnum.INTERNAL_USER.securityRole());
    }
}
