package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.PartitioningDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
@Slf4j
@RequiredArgsConstructor
public class PartitioningController {

    private final PartitioningService partitioningService;

    private final CampaignService campaignService;

    private final ModelMapper modelmapper;

    private final QuestioningService questioningService;

    @Operation(summary = "Search for partitionings by the campaign id")
    @GetMapping(value = UrlConstants.API_CAMPAIGNS_ID_PARTITIONINGS, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || hasPermission(null, 'READ_SUPPORT')")
    public ResponseEntity<List<PartitioningDto>> getPartitioningsByCampaign(@PathVariable("id") String id) {
        Campaign campaign = campaignService.findById(id);
        return ResponseEntity.ok()
                .body(campaign.getPartitionings().stream().map(this::convertToDto)
                        .toList());


    }

    @Operation(summary = "Search for a partitioning by its id")
    @GetMapping(value = UrlConstants.API_PARTITIONINGS_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PartitioningDto> getPartitioning(@PathVariable("id") String id) {
        Partitioning partitioning = partitioningService.findById(StringUtils.upperCase(id));
        return ResponseEntity.ok().body(convertToDto(partitioning));


    }

    @Operation(summary = "Update or create a partitioning")
    @PutMapping(value = UrlConstants.API_PARTITIONINGS_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PartitioningDto.class))),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = PartitioningDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<PartitioningDto> putPartitioning(@PathVariable("id") String id,
                                                           @RequestBody PartitioningDto partitioningDto) {
        if (!partitioningDto.getId().equalsIgnoreCase(id)) {
            throw new NotMatchException("id and owner id don't match");
        }
        Partitioning partitioning;

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(partitioningDto.getId()).toUriString());
        HttpStatus httpStatus;

        try {
            partitioningService.findById(id);
            log.info("Update partitioning with the id {}", partitioningDto.getId());
            httpStatus = HttpStatus.OK;
        } catch (NotFoundException e) {
            log.info("Create partitioning with the id {}", partitioningDto.getId());
            httpStatus = HttpStatus.CREATED;
        }
        partitioning = partitioningService.insertOrUpdatePartitioning(convertToEntity(partitioningDto));
        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(convertToDto(partitioning));
    }

    @Operation(summary = "Delete a partitioning, its partitionings, partitionings, questionings ...")
    @DeleteMapping(value = UrlConstants.API_PARTITIONINGS_ID)
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @Transactional
    public void deletePartitioning(@PathVariable("id") String id) {
        Partitioning partitioning = partitioningService.findById(id);
        partitioningService.deletePartitioningById(id);

        int nbQuestioningDeleted = questioningService.deleteQuestioningsOfOnePartitioning(partitioning);
        log.info("Partitioning {} deleted - {} questionings deleted", id, nbQuestioningDeleted);
    }

    private PartitioningDto convertToDto(Partitioning partitioning) {
        return modelmapper.map(partitioning, PartitioningDto.class);
    }

    private Partitioning convertToEntity(PartitioningDto partitioningDto) {
        return modelmapper.map(partitioningDto, Partitioning.class);
    }

}
