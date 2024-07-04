package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthConstants;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.service.MySurveysService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "4 - Cross domain")
@RequiredArgsConstructor
public class MyQuestioningsController {

    private final MySurveysService mySurveysService;

    private final ApplicationConfig config;

    @GetMapping(value = Constants.API_MY_QUESTIONINGS_ID)
    @PreAuthorize("hasRole('INTERNAL_USER') "
            + "|| hasRole('WEB_CLIENT') "
            + "|| @AuthorizeMethodDecider.isRespondent()"
            + "|| hasRole('ADMIN') ")
    public List<MyQuestioningDto> findById() {

        String idec;

        if (config.getAuthType().equals(AuthConstants.OIDC)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            final Jwt jwt = (Jwt) authentication.getPrincipal();
            idec=jwt.getClaimAsString(config.getIdClaim()).toUpperCase();
        }
        else{
           idec="anonymous";
        }

        List<MyQuestioningDto> listSurveys = mySurveysService.getListMySurveys(idec);

        return listSurveys;

    }
}
