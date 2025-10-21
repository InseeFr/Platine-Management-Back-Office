package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.exception.ForbiddenAccessException;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningInformationsDto;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import fr.insee.survey.datacollectionmanagement.query.service.QuestioningInformationsService;
import fr.insee.survey.datacollectionmanagement.query.validation.ValidUserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
@Slf4j
@Tag(name = "7 - Webclients", description = "Endpoints for webclients")
@RequiredArgsConstructor
@Validated
public class QuestioningInformationsController {

    private final QuestioningInformationsService questioningInformationsService;

    private final CheckHabilitationService checkHabilitationService;


    @Operation(summary = "Informations to fill in an Orbeon questionnaire")
    @GetMapping(value = UrlConstants.API_WEBCLIENT_INFORMATIONS, produces = MediaType.APPLICATION_XML_VALUE)
    public QuestioningInformationsDto getQuestioningInformations(@PathVariable("idCampaign") String idCampaign,
                                                                 @PathVariable("idUE") String idsu,
                                                                 @Valid @ValidUserRole @RequestParam(value = "role", required = false) String role,
                                                                 @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        List<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String userId = authentication.getName().toUpperCase();
        boolean habilitated = checkHabilitationService.checkHabilitation(role, idsu, idCampaign, userRoles, userId);
        if (habilitated) {
            if (StringUtils.equalsIgnoreCase(role, UserRoles.INTERVIEWER)) {
                String idContact = authentication.getName().toUpperCase();
                log.info("Get orbeon questioning informations for interviewer {} : campaign = {} and survey unit = {}", idContact, idCampaign, idsu);
                return questioningInformationsService.findQuestioningInformationsDtoInterviewer(idCampaign, idsu, idContact);

            }
            log.info("Get orbeon questioning informations for reviewer : campaign = {} and survey unit = {}", idCampaign, idsu);
            return questioningInformationsService.findQuestioningInformationsDtoReviewer(idCampaign, idsu);
        }
        throw new ForbiddenAccessException(String.format("User %s not authorized", authentication.getName()));
    }
}
