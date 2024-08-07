package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitComment;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitCommentInputDto;
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
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.Set;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SurveyUnitCommentController {

    private final SurveyUnitService surveyUnitService;
    private final SurveyUnitCommentService surveyUnitCommentService;
    private final ModelMapper modelMapper;


    @Operation(summary = "Create a survey unit comment")
    @PostMapping(value = Constants.API_SURVEY_UNITS_ID_COMMENT, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = SurveyUnitCommentInputDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity postSurveyUnitComment(@PathVariable String id, @Valid @RequestBody SurveyUnitCommentInputDto surveyUnitCommentDto) {

        SurveyUnit surveyUnit = surveyUnitService.findbyId(id);
        SurveyUnitComment surveyUnitComment = convertToEntity(surveyUnitCommentDto);
        surveyUnitComment.setDate(new Date());
        SurveyUnitComment newSurveyUnitComment = surveyUnitCommentService.saveSurveyUnitComment(surveyUnitComment);
        Set<SurveyUnitComment> setSurveyUnitComments = surveyUnit.getSurveyUnitComments();
        setSurveyUnitComments.add(newSurveyUnitComment);
        surveyUnit.setSurveyUnitComments(setSurveyUnitComments);
        surveyUnitService.saveSurveyUnit(surveyUnit);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders)
                .body(convertToDto(newSurveyUnitComment));


    }

    private SurveyUnitComment convertToEntity(SurveyUnitCommentInputDto surveyUnitCommentDto) {
        return modelMapper.map(surveyUnitCommentDto, SurveyUnitComment.class);
    }

    private SurveyUnitCommentInputDto convertToDto(SurveyUnitComment surveyUnitComment) {
        return modelMapper.map(surveyUnitComment, SurveyUnitCommentInputDto.class);
    }


}
