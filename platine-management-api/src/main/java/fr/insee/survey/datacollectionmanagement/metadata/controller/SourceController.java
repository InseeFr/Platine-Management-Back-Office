package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.dto.OpenDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SourceDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SourceOnlineStatusDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.*;
import fr.insee.survey.datacollectionmanagement.metadata.util.ParamValidator;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SourceController {

    private final SourceService sourceService;

    private final OwnerService ownerService;

    private final SupportService supportService;

    private final ViewService viewService;

    private final ModelMapper modelmapper;

    private final QuestioningService questioningService;

    private final CampaignService campaignService;

    private final ParametersService parametersService;

    @Operation(summary = "Search for sources, paginated")
    @GetMapping(value = Constants.API_SOURCES, produces = "application/json")
    public List<SourceDto> getSources() {
        return sourceService.findAll().stream().map(this::convertToDto).toList();
    }

    @Operation(summary = "Search for a source by its id")
    @GetMapping(value = Constants.API_SOURCES_ID, produces = "application/json")
    public ResponseEntity<SourceOnlineStatusDto> getSource(@PathVariable("id") String id) {
        Source source = sourceService.findById(StringUtils.upperCase(id));
        return ResponseEntity.ok().body(convertToCompleteDto(source));

    }

    @Operation(summary = "Update or create a source")
    @PutMapping(value = Constants.API_SOURCES_ID, produces = "application/json", consumes = "application/json")
    public ResponseEntity<SourceOnlineStatusDto> putSource(@PathVariable("id") String id, @RequestBody @Valid SourceOnlineStatusDto sourceOnlineStatusDto) {
        if (!sourceOnlineStatusDto.getId().equalsIgnoreCase(id)) {
            throw new NotMatchException("id and source id don't match");

        }

        Source source;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(sourceOnlineStatusDto.getId()).toUriString());
        HttpStatus httpStatus;
        try {
            sourceService.findById(id);
            log.warn("Update source with the id {}", sourceOnlineStatusDto.getId());
            httpStatus = HttpStatus.OK;
        } catch (NotFoundException e) {
            log.info("Create source with the id {}", sourceOnlineStatusDto.getId());
            httpStatus = HttpStatus.CREATED;
        }


        source = sourceService.insertOrUpdateSource(convertToEntity(sourceOnlineStatusDto));
        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(convertToCompleteDto(source));
    }

    @Operation(summary = "Delete a source, its surveys, campaigns, partitionings, questionings ...")
    @DeleteMapping(value = Constants.API_SOURCES_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteSource(@PathVariable("id") String id) {
        int nbQuestioningDeleted = 0;
        int nbViewDeleted = 0;
        Source source = sourceService.findById(id);

        List<Campaign> listCampaigns = new ArrayList<>();
        List<Partitioning> listPartitionings = new ArrayList<>();

        source.getSurveys().forEach(su -> listCampaigns.addAll(su.getCampaigns()));
        source.getSurveys().forEach(
                su -> su.getCampaigns().forEach(c -> listPartitionings.addAll(c.getPartitionings())));

        for (Campaign campaign : listCampaigns) {
            nbViewDeleted += viewService.deleteViewsOfOneCampaign(campaign);
        }
        for (Partitioning partitioning : listPartitionings) {
            nbQuestioningDeleted += questioningService.deleteQuestioningsOfOnePartitioning(partitioning);
        }
        sourceService.deleteSourceById(id);

        log.info("Source {} deleted with all its metadata children - {} questioning deleted - {} view deleted", id,
                nbQuestioningDeleted, nbViewDeleted);

    }

    @Operation(summary = "Check if a source is opened")
    @GetMapping(value = Constants.API_SOURCE_ID_OPENED, produces = "application/json")
    public OpenDto isSourceOpened(@PathVariable("id") String id) {

        Source source = sourceService.findById(id.toUpperCase());
        if (Boolean.TRUE.equals(source.getForceClose())) {
            return new OpenDto(false, true, source.getMessageSurveyOffline(), source.getMessageInfoSurveyOffline());
        }

        if (source.getSurveys().isEmpty())
            return new OpenDto(true, false, source.getMessageSurveyOffline(), source.getMessageInfoSurveyOffline());

        boolean isOpened = source.getSurveys().stream().flatMap(survey -> survey.getCampaigns().stream()).anyMatch(campaignService::isCampaignOngoing);

        return new OpenDto(isOpened, false, source.getMessageSurveyOffline(), source.getMessageInfoSurveyOffline());

    }

    @Operation(summary = "Search for surveys by the owner id")
    @GetMapping(value = Constants.API_OWNERS_ID_SOURCES, produces = "application/json")
    public ResponseEntity<List<SourceDto>> getSourcesByOwner(@PathVariable("id") String id) {
        Owner owner = ownerService.findById(id);
        return ResponseEntity.ok()
                .body(owner.getSources().stream().map(this::convertToDto).toList());


    }

    @Operation(summary = "Get source parameters")
    @GetMapping(value = Constants.API_SOURCES_ID_PARAMS, produces = "application/json")
    public ResponseEntity<List<ParamsDto>> getParams(@PathVariable("id") String id) {
        Source source = sourceService.findById(StringUtils.upperCase(id));
        List<ParamsDto> listParams = source.getParams().stream().map(parametersService::convertToDto).toList();
        return ResponseEntity.ok().body(listParams);
    }


    @Operation(summary = "Create a parameter for a source")
    @PutMapping(value = Constants.API_SOURCES_ID_PARAMS, produces = "application/json")
    public void putParams(@PathVariable("id") String id, @RequestBody @Valid ParamsDto paramsDto) {
        Source source = sourceService.findById(StringUtils.upperCase(id));

        ParamValidator.validateParams(paramsDto);
        Parameters param = parametersService.convertToEntity(paramsDto);
        param.setMetadataId(StringUtils.upperCase(id));
        Set<Parameters> updatedParams = parametersService.updateSourceParams(source,param);
        source.setParams(updatedParams);
        sourceService.insertOrUpdateSource(source);
    }



    private SourceDto convertToDto(Source source) {
        return modelmapper.map(source, SourceDto.class);
    }

    private SourceOnlineStatusDto convertToCompleteDto(Source source) {
        return modelmapper.map(source, SourceOnlineStatusDto.class);
    }


    private Source convertToEntity(SourceOnlineStatusDto sourceOnlineStatusDto) {
        return modelmapper.map(sourceOnlineStatusDto, Source.class);
    }


}
