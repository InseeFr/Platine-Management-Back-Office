package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.ParametersService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.AssistanceDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningIdDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Slf4j
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@RequiredArgsConstructor
public class QuestioningController {

    private final QuestioningService questioningService;

    private final SurveyUnitService surveyUnitService;

    private final PartitioningService partitioningService;

    private final ParametersService parametersService;

    private final ModelMapper modelMapper;


    @Operation(summary = "Create or update questioning")
    @PostMapping(value = UrlConstants.API_QUESTIONINGS, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = QuestioningDto.class))),
            @ApiResponse(responseCode = "404", description = "NotFound")
    })
    @Deprecated
    public ResponseEntity<?> postQuestioning(@RequestBody QuestioningDto questioningDto) {
        log.warn("DEPRECATED");
        SurveyUnit su = surveyUnitService.findbyId(questioningDto.getSurveyUnitId());
        partitioningService.findById(questioningDto.getIdPartitioning());


        Questioning questioning = convertToEntity(questioningDto);
        questioning.setSurveyUnit(su);
        questioning = questioningService.saveQuestioning(questioning);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION, ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders).body(convertToDto(questioning));

    }

    @Operation(summary = "Search for questionings by survey unit id")
    @GetMapping(value = UrlConstants.API_SURVEY_UNITS_ID_QUESTIONINGS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestioningDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<?> getQuestioningsBySurveyUnit(@PathVariable("id") String id) {
        SurveyUnit su = surveyUnitService.findbyId(StringUtils.upperCase(id));
        return new ResponseEntity<>(su.getQuestionings().stream().map(this::convertToDto).toList(), HttpStatus.OK);

    }

    @Operation(summary = "Get questioning assistance mail")
    @GetMapping(value = UrlConstants.API_QUESTIONINGS_ID_ASSISTANCE, produces = "application/json")
    public AssistanceDto getAssistanceQuestioning(@PathVariable("id") Long questioningId) {
        Questioning questioning = questioningService.findbyId(questioningId);
        Partitioning part = partitioningService.findById(questioning.getIdPartitioning());
        String mail = parametersService.findSuitableParameterValue(part, ParameterEnum.MAIL_ASSISTANCE);
        return new AssistanceDto(mail, questioning.getSurveyUnit().getIdSu());
    }

    @Operation(summary = "Get questioning id for a campaignId and and a surveyUnitId")
    @GetMapping(value = UrlConstants.API_QUESTIONINGSID, produces = "application/json")
    public QuestioningIdDto getQuestioningId(@RequestParam("campaignId") String campaignId, @RequestParam("surveyUnitId") String surveyUnitId) {
        return questioningService.findByCampaignIdAndSurveyUnitIdSu(campaignId, surveyUnitId);
    }


    private Questioning convertToEntity(QuestioningDto questioningDto) {
        return modelMapper.map(questioningDto, Questioning.class);
    }

    private QuestioningDto convertToDto(Questioning questioning) {
        return modelMapper.map(questioning, QuestioningDto.class);
    }

}
