package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Owner;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SourceCompleteDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.OwnerService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SupportService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
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

    @Operation(summary = "Search for sources, paginated")
    @GetMapping(value = Constants.API_SOURCES, produces = "application/json")
    public ResponseEntity<SourcePage> getSources(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Source> pageSource = sourceService.findAll(pageable);
        List<SourceCompleteDto> listSources = pageSource.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(new SourcePage(listSources, pageable, pageSource.getTotalElements()));
    }

    @Operation(summary = "Search for a source by its id")
    @GetMapping(value = Constants.API_SOURCES_ID, produces = "application/json")
    public ResponseEntity<SourceCompleteDto> getSource(@PathVariable("id") String id) {
        Source source = sourceService.findById(StringUtils.upperCase(id));
        source = sourceService.findById(StringUtils.upperCase(id));
        return ResponseEntity.ok().body(convertToDto(source));

    }

    @Operation(summary = "Update or create a source")
    @PutMapping(value = Constants.API_SOURCES_ID, produces = "application/json", consumes = "application/json")
    public ResponseEntity<SourceCompleteDto> putSource(@PathVariable("id") String id, @RequestBody @Valid SourceCompleteDto SourceCompleteDto) {
        if (!SourceCompleteDto.getId().equalsIgnoreCase(id)) {
            throw new NotMatchException("id and source id don't match");

        }

        Source source;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(SourceCompleteDto.getId()).toUriString());
        HttpStatus httpStatus;
        try {
            sourceService.findById(id);
            log.warn("Update source with the id {}", SourceCompleteDto.getId());
            httpStatus = HttpStatus.OK;
        } catch (NotFoundException e) {
            log.info("Create source with the id {}", SourceCompleteDto.getId());
            httpStatus = HttpStatus.CREATED;
        }


        source = sourceService.insertOrUpdateSource(convertToEntity(SourceCompleteDto));
        if (source.getOwner() != null && httpStatus.equals(HttpStatus.CREATED))
            ownerService.addSourceFromOwner(source.getOwner(), source);
        if (source.getSupport() != null && httpStatus.equals(HttpStatus.CREATED))
            supportService.addSourceFromSupport(source.getSupport(), source);

        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(convertToDto(source));
    }

    @Operation(summary = "Delete a source, its surveys, campaigns, partitionings, questionings ...")
    @DeleteMapping(value = Constants.API_SOURCES_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteSource(@PathVariable("id") String id) {
        int nbQuestioningDeleted = 0, nbViewDeleted = 0;
        Source source = sourceService.findById(id);

        if (source.getOwner() != null)
            ownerService.removeSourceFromOwner(source.getOwner(), source);

        if (source.getSupport() != null)
            supportService.removeSourceFromSupport(source.getSupport(), source);

        sourceService.deleteSourceById(id);
        List<Campaign> listCampaigns = new ArrayList<>();
        List<Partitioning> listPartitionings = new ArrayList<>();

        source.getSurveys().stream().forEach(su -> listCampaigns.addAll(su.getCampaigns()));
        source.getSurveys().stream().forEach(
                su -> su.getCampaigns().stream().forEach(c -> listPartitionings.addAll(c.getPartitionings())));

        for (Campaign campaign : listCampaigns) {
            nbViewDeleted += viewService.deleteViewsOfOneCampaign(campaign);
        }
        for (Partitioning partitioning : listPartitionings) {
            nbQuestioningDeleted += questioningService.deleteQuestioningsOfOnePartitioning(partitioning);
        }
        log.info("Source {} deleted with all its metadata children - {} questioning deleted - {} view deleted", id,
                nbQuestioningDeleted, nbViewDeleted);

    }

    @Operation(summary = "Search for surveys by the owner id")
    @GetMapping(value = Constants.API_OWNERS_ID_SOURCES, produces = "application/json")
    public ResponseEntity<?> getSourcesByOwner(@PathVariable("id") String id) {
        Owner owner = ownerService.findById(id);
        return ResponseEntity.ok()
                .body(owner.getSources().stream().map(this::convertToDto).toList());


    }

    private SourceCompleteDto convertToDto(Source source) {
        return modelmapper.map(source, SourceCompleteDto.class);
    }

    private Source convertToEntity(SourceCompleteDto SourceCompleteDto) {
        return modelmapper.map(SourceCompleteDto, Source.class);
    }

    class SourcePage extends PageImpl<SourceCompleteDto> {

        public SourcePage(List<SourceCompleteDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
