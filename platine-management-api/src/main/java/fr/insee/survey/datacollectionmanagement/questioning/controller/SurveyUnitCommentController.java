package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitCommentInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitCommentOutputDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitCommentService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SurveyUnitCommentController {

    private final SurveyUnitCommentService surveyUnitCommentService;
    private final SurveyUnitService surveyUnitService;

    @Operation(summary = "Create a survey unit comment")
    @PostMapping(value = UrlConstants.API_SURVEY_UNITS_ID_COMMENT, produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = SurveyUnitCommentInputDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public SurveyUnitCommentOutputDto postSurveyUnitComment(@PathVariable String id, @Valid @RequestBody SurveyUnitCommentInputDto surveyUnitCommentDto) {

        SurveyUnit surveyUnit = surveyUnitService.findbyId(id);
        return  surveyUnitCommentService.saveSurveyUnitComment(surveyUnit, surveyUnitCommentDto);

    }




}
