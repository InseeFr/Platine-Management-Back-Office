package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthUser;
import fr.insee.survey.datacollectionmanagement.config.auth.user.UserProvider;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.query.dto.HabilitationDto;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "4 - Cross domain")
public class CheckHabilitationController {


    @Autowired
    private CheckHabilitationService checkHabilitationService;

    @Autowired
    UserProvider userProvider;

    @PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
            + "|| @AuthorizeMethodDecider.isWebClient() "
            + "|| @AuthorizeMethodDecider.isRespondent()"
            + "|| @AuthorizeMethodDecider.isAdmin()")
    @GetMapping(path = Constants.API_CHECK_HABILITATION,produces = "application/json")
    public ResponseEntity<HabilitationDto> checkHabilitation(
            @RequestParam(required = false) String role,
            @RequestParam(required = true) String id,
            @RequestParam(required = true) String campaign,
            Authentication authentication) {
        AuthUser authUser = userProvider.getUser(authentication);
        HabilitationDto habDto =  new HabilitationDto();
        boolean habilitated = checkHabilitationService.checkHabilitation(role, id,campaign, authUser);
        habDto.setHabilitated(habilitated);
        return new ResponseEntity<>(habDto, HttpStatus.OK);


    }

}
