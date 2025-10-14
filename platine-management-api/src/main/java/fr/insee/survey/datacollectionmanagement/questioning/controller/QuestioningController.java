package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.AssistanceDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningIdDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@RequiredArgsConstructor
public class QuestioningController {

    private final QuestioningService questioningService;

    private final SurveyUnitService surveyUnitService;

    private final PartitioningService partitioningService;

    private final ModelMapper modelMapper;

    private final QuestioningAccreditationService questioningAccreditationService;

    /**
     * @deprecated
     */
    @Operation(summary = "Create or update questioning")
    @PostMapping(value = UrlConstants.API_QUESTIONINGS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = QuestioningDto.class))),
            @ApiResponse(responseCode = "404", description = "NotFound")
    })
    @Deprecated(since = "3.4.0")
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
    public ResponseEntity<QuestioningDto> postQuestioning(@RequestBody QuestioningDto questioningDto) {
        log.warn("DEPRECATED");
        SurveyUnit su = surveyUnitService.findbyId(questioningDto.getSurveyUnitId());
        partitioningService.getById(questioningDto.getIdPartitioning());


        Questioning questioning = convertToEntity(questioningDto);
        questioning.setSurveyUnit(su);
        questioning = questioningService.saveQuestioning(questioning);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION, ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders).body(convertToDto(questioning));

    }

    @Operation(summary = "Search for questionings by survey unit id")
    @GetMapping(value = UrlConstants.API_SURVEY_UNITS_ID_QUESTIONINGS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestioningDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
    public ResponseEntity<List<QuestioningDto>> getQuestioningsBySurveyUnit(@PathVariable("id") String id) {
        SurveyUnit su = surveyUnitService.findbyId(StringUtils.upperCase(id));
        return new ResponseEntity<>(su.getQuestionings().stream().map(this::convertToDto).toList(), HttpStatus.OK);

    }

    @Operation(summary = "Get questioning assistance mail")
    @GetMapping(value = UrlConstants.API_QUESTIONINGS_ID_ASSISTANCE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_PORTAL_PRIVILEGES)
    public AssistanceDto getAssistanceQuestioning(@PathVariable("id") UUID questioningId) {
        return questioningService.getMailAssistanceDto(questioningId);
    }

    @Operation(summary = "Get questioning id for a campaignId and and a surveyUnitId")
    @GetMapping(value = UrlConstants.API_QUESTIONINGSID, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
    public QuestioningIdDto getQuestioningId(@RequestParam("campaignId") String campaignId, @RequestParam("surveyUnitId") String surveyUnitId) {
        return questioningService.findByCampaignIdAndSurveyUnitIdSu(campaignId, surveyUnitId);
    }

    @Operation(summary = "Give questioning main accreditation to target contact")
    @PutMapping(value = UrlConstants.API_MAIN_CONTACT_INTERROGATIONS_ASSIGN)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
    public void updateInterrogationToMainContactAsMain(
            @PathVariable("interrogationId") UUID interrogationId,
            @PathVariable("contactId") String contactId)  {

        questioningAccreditationService.setMainQuestioningAccreditationToContact(contactId, interrogationId, false);
    }

    private Questioning convertToEntity(QuestioningDto questioningDto) {
        return modelMapper.map(questioningDto, Questioning.class);
    }

    private QuestioningDto convertToDto(Questioning questioning) {
        return modelMapper.map(questioning, QuestioningDto.class);
    }

}
