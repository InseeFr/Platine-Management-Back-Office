package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.dto.BusinessContactsDto;
import fr.insee.survey.datacollectionmanagement.contact.service.BusinessContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "6 - Webclients", description = "Enpoints for webclients")
@Slf4j
@RequiredArgsConstructor
public class BusinessContactController {


    private final BusinessContactService businessContactService;

    @Operation(summary = "Search for the main contact by campaign and survey unit")
    @GetMapping(value = UrlConstants.API_WEBCLIENT_BUSINESS_MAIN_CONTACT)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || " + AuthorityPrivileges.HAS_RESPONDENT_LIMITED_PRIVILEGES)
    public BusinessContactsDto getListBusinessContact(@PathVariable("campaignId") String campaignId,
                                                      @PathVariable("surveyUnitId") String surveyUnitId) {
        return businessContactService.findMainContactByCampaignAndSurveyUnit(campaignId, surveyUnitId);

    }
}
