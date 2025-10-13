package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitEventRequestDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitEventResponseDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "3 - Survey units", description = "Enpoints to create, update, delete and find survey units")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SurveyUnitEventController {

    private final SurveyUnitEventService surveyUnitEventService;

    @Operation(summary = "Get survey unit events")
    @GetMapping(value = UrlConstants.API_SURVEY_UNITS_ID_EVENTS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SurveyUnitEventResponseDto> getEvents(@PathVariable(value = "id") String surveyUnitId) {
        return surveyUnitEventService.getEvents(surveyUnitId);
    }

    @Operation(summary = "Create a survey unit event")
    @PostMapping(value = UrlConstants.API_SURVEY_UNITS_ID_EVENTS,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void postEvent(@PathVariable("id") String surveyUnitId,
                          @RequestBody @Valid SurveyUnitEventRequestDto eventDto) {
        surveyUnitEventService.createEvent(eventDto, surveyUnitId);
    }
}
