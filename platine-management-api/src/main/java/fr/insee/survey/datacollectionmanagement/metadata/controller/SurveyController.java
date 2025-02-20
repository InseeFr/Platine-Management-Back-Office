package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SurveyDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
@Slf4j
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    private final SourceService sourceService;

    private final ViewService viewService;

    private final ModelMapper modelmapper;

    private final QuestioningService questioningService;

    @Operation(summary = "Search for surveys, paginated")
    @GetMapping(value = UrlConstants.API_SURVEYS, produces = "application/json")
    public ResponseEntity<SurveyController.SurveyPage> getSurveys(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Survey> pageSurvey = surveyService.findAll(pageable);
        List<SurveyDto> listSurveys = pageSurvey.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(new SurveyController.SurveyPage(listSurveys, pageable, pageSurvey.getTotalElements()));
    }

    @Operation(summary = "Search for surveys, paginated")
    @GetMapping(value = UrlConstants.API_SURVEYS_SEARCH, produces = "application/json")
    public ResponseEntity<SurveyController.SurveyPage> getSurveys(
            @RequestParam(required = false) String idSource,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String periodicity,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Survey> pageSurvey = surveyService.findBySourceIdYearPeriodicity(pageable, idSource, year, periodicity);
        List<SurveyDto> listSurveys = pageSurvey.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(new SurveyController.SurveyPage(listSurveys, pageable, pageSurvey.getTotalElements()));
    }

    @Operation(summary = "Search for surveys by the source id")
    @GetMapping(value = UrlConstants.API_SOURCES_ID_SURVEYS, produces = "application/json")
    public ResponseEntity<List<SurveyDto>> getSurveysBySource(@PathVariable("id") String id) {
        Source source = sourceService.findById(id);
        return ResponseEntity.ok()
                .body(source.getSurveys().stream().map(this::convertToDto).toList());


    }

    @Operation(summary = "Search for a survey by its id")
    @GetMapping(value = UrlConstants.API_SURVEYS_ID, produces = "application/json")
    public ResponseEntity<SurveyDto> getSurvey(@PathVariable("id") String id) {
        Survey survey = surveyService.findById(StringUtils.upperCase(id));
        return ResponseEntity.ok().body(convertToDto(survey));


    }

    @Operation(summary = "Update or create a survey")
    @PutMapping(value = UrlConstants.API_SURVEYS_ID, produces = "application/json", consumes = "application/json")
    public ResponseEntity<SurveyDto> putSurvey(@PathVariable("id") String id, @RequestBody @Valid SurveyDto surveyDto) {
        if (!surveyDto.getId().equalsIgnoreCase(id)) {
            throw new NotMatchException("id and idSurvey don't match");


        }
        Survey survey;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(surveyDto.getId()).toUriString());
        HttpStatus httpStatus;

        try {
            surveyService.findById(id);
            log.info("Update survey with the id {}", surveyDto.getId());
            httpStatus = HttpStatus.OK;
        } catch (NotFoundException e) {
            log.info("Creating survey with the id {}", surveyDto.getId());
            httpStatus = HttpStatus.CREATED;
        }

        survey = surveyService.insertOrUpdateSurvey(convertToEntity(surveyDto));
        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(convertToDto(survey));
    }

    @Operation(summary = "Delete a survey, its campaigns, partitionings, questionings ...")
    @DeleteMapping(value = UrlConstants.API_SURVEYS_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteSurvey(@PathVariable("id") String id) {
        Survey survey = surveyService.findById(id);

        int nbQuestioningDeleted = 0;
        int nbViewDeleted = 0;

        surveyService.deleteSurveyById(id);
        List<Partitioning> listPartitionings = new ArrayList<>();

        survey.getCampaigns().stream().forEach(c -> listPartitionings.addAll(c.getPartitionings()));

        for (Campaign campaign : survey.getCampaigns()) {
            viewService.findViewByCampaignId(campaign.getId()).stream()
                    .forEach(v -> viewService.deleteView(v));
        }
        for (Partitioning partitioning : listPartitionings) {
            questioningService.findByIdPartitioning(partitioning.getId()).stream()
                    .forEach(q -> questioningService.deleteQuestioning(q.getId()));
        }

        for (Campaign campaign : survey.getCampaigns()) {
            nbViewDeleted += viewService.deleteViewsOfOneCampaign(campaign);
        }
        for (Partitioning partitioning : listPartitionings) {
            nbQuestioningDeleted += questioningService.deleteQuestioningsOfOnePartitioning(partitioning);
        }
        log.info("Source {} deleted and all its metadata children - {} questioning deleted - {} view deleted", id,
                nbQuestioningDeleted, nbViewDeleted);

    }

    private SurveyDto convertToDto(Survey survey) {
        return modelmapper.map(survey, SurveyDto.class);
    }

    private Survey convertToEntity(SurveyDto surveyDto) {
        return modelmapper.map(surveyDto, Survey.class);
    }

    class SurveyPage extends PageImpl<SurveyDto> {

        public SurveyPage(List<SurveyDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
