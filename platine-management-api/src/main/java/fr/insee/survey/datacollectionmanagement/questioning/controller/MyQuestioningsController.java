package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import fr.insee.survey.datacollectionmanagement.query.service.MySurveysService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "1 - Contacts", description = "Endpoints to create, update, delete and find contacts")
@RequiredArgsConstructor
public class MyQuestioningsController {

    private final MySurveysService mySurveysService;

    private final ApplicationConfig config;

    @GetMapping(value = UrlConstants.API_MY_QUESTIONINGS_ID)
    @PreAuthorize(AuthorityPrivileges.HAS_RESPONDENT_PRIVILEGES)
    public List<MyQuestioningDto> findById(@CurrentSecurityContext(expression = "authentication.name")
                                           String idec) {

        return mySurveysService.getListMySurveys(idec.toUpperCase());
    }

    @GetMapping(value = UrlConstants.API_MY_QUESTIONNAIRES)
    @PreAuthorize(AuthorityPrivileges.HAS_RESPONDENT_PRIVILEGES)
    public List<MyQuestionnaireDto> getMyQuestionnaires(@CurrentSecurityContext(expression = "authentication.name")
                                           String idec) {
        return mySurveysService.getListMyQuestionnaires(idec.toUpperCase(), config.getQuestionnaireApiUrl());
    }
}
