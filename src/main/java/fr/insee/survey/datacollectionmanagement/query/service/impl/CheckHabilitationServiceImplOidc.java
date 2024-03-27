package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthUser;
import fr.insee.survey.datacollectionmanagement.constants.AuthConstants;
import fr.insee.survey.datacollectionmanagement.constants.CheckHabilitationsRoles;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "fr.insee.datacollectionmanagement.auth.mode", havingValue = AuthConstants.OIDC)
@Slf4j
public class CheckHabilitationServiceImplOidc implements CheckHabilitationService {

    private final ApplicationConfig applicationConfig;

    private final ViewService viewService;

    private final UserService userService;

    @Override
    public boolean checkHabilitation(String role, String idSu, String campaignId, AuthUser authUser) {

        String userId = authUser.getId().toUpperCase();

        //admin
        if (isUserInRole(authUser.getRoles(), applicationConfig.getRoleAdmin())) {
            log.info("Check habilitation of admin {} for accessing survey-unit {} of campaign {} resulted in true", userId, idSu, campaignId);
            return true;
        }

        //respondents
        if (role == null || role.isBlank() || role.equals(CheckHabilitationsRoles.INTERVIEWER)) {
            if (isUserInRole(authUser.getRoles(), applicationConfig.getRoleRespondent())) {
                boolean habilitated = viewService.countViewByIdentifierIdSuCampaignId(userId.toUpperCase(), idSu, campaignId) != 0;
                log.info("Check habilitation of interviewer {} for accessing survey-unit {} of campaign {} resulted in {}", userId, idSu, campaignId, habilitated);
                return habilitated;
            }
            log.warn("Check habilitation of interviewer {} for accessing survey-unit {} of campaign {} - no respondent habilitation found in token - check habilitation: false", userId, idSu, campaignId);
            return false;
        }


        // internal users
        if (!role.equals(CheckHabilitationsRoles.REVIEWER)) {
            log.warn("User {} - internal user habilitation not found in token - Check habilitation:false", userId);
            return false;
        }
        User user;
        try {
            user = userService.findByIdentifier(userId);
        } catch (NotFoundException e) {
            log.warn("User '{}' doesn't exists", userId);
            return false;
        }


        if (isUserInRole(authUser.getRoles(), applicationConfig.getRoleInternalUser())) {
            String userRole = user.getRole().toString();
            if (userRole.equals(User.UserRoleType.ASSISTANCE.toString())) {
                log.warn("User '{}' has assistance profile - check habilitation: false", userId);
                return false;
            }
            log.warn("User '{}' has {} profile - check habilitation: true", userId, userRole);
            return true;


        }
        log.warn("Only '{}' and '{}' are accepted as a role in query argument", CheckHabilitationsRoles.REVIEWER, CheckHabilitationsRoles.INTERVIEWER);
        return false;

    }

    private boolean isUserInRole(List<String> roles, List<String> role) {

        return role.stream().anyMatch(roles::contains);
    }

}
