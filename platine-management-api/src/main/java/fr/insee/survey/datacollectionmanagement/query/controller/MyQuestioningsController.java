package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestioningDto;
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


    @GetMapping(value = Constants.API_MY_QUESTIONINGS_ID)
    @PreAuthorize(AuthorityPrivileges.HAS_REPONDENT_PRIVILEGES)
    public List<MyQuestioningDto> findById(@CurrentSecurityContext(expression = "authentication.name")
                                           String idec) {


        return mySurveysService.getListMySurveys(idec.toUpperCase());

    }
}
