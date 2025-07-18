package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.ImpossibleToDeleteException;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.*;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import fr.insee.survey.datacollectionmanagement.metadata.util.ParamValidator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Upload;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;

import static fr.insee.survey.datacollectionmanagement.metadata.documentation.examples.CampaignDocumentation.*;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "3 - Metadata", description = "Endpoints to create, update, delete and find entities in metadata domain")
@Slf4j
@Validated
@RequiredArgsConstructor
public class CampaignController {


    private final CampaignService campaignService;

    private final SurveyService surveyService;

    private final ViewService viewService;

    private final QuestioningService questioningService;

    private final UploadService uploadService;

    private final ModelMapper modelmapper;


    @Operation(summary = "Search for campaigns, paginated")
    @GetMapping(value = UrlConstants.API_CAMPAIGNS, produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignPage getCampaigns(

            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Campaign> pageCampaign = campaignService.findAll(pageable);
        List<CampaignDto> listCampaigns = pageCampaign.stream().map(this::convertToDto).toList();
        return new CampaignPage(listCampaigns, pageable, pageCampaign.getTotalElements());
    }

    @Operation(summary = "Search for campaigns by the survey id")
    @GetMapping(value = UrlConstants.API_SURVEYS_ID_CAMPAIGNS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CampaignDto> getCampaignsBySurvey(@PathVariable("id") String id) {

        Survey survey = surveyService.findById(id);
        return survey.getCampaigns().stream().map(this::convertToDto).toList();

    }

    @Operation(summary = "Search for campaigns and partitionings by the survey id")
    @GetMapping(value = UrlConstants.API_SURVEYS_ID_CAMPAIGNS_PARTITIONINGS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CampaignPartitioningsDto> getCampaignsPartitioningsBySurvey(@PathVariable("id") String id) {

        Survey survey = surveyService.findById(id);
        return survey.getCampaigns().stream().map(this::convertToCampaignPartitioningsDto).toList();

    }

    @Operation(summary = "Search for a campaign by its id")
    @GetMapping(value = UrlConstants.API_CAMPAIGNS_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CampaignDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<CampaignDto> getCampaign(@PathVariable("id") String id) {
        Campaign campaign = campaignService.findById(StringUtils.upperCase(id));
        return ResponseEntity.ok().body(convertToDto(campaign));


    }

    @Operation(summary = "Get campaign parameters")
    @GetMapping(value = UrlConstants.API_CAMPAIGNS_ID_PARAMS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ParamsDto> getParams(@PathVariable("id") String id) {
        Campaign campaign = campaignService.findById(StringUtils.upperCase(id));
        return campaign.getParams().stream().map(this::convertToDto).toList();
    }

    @Operation(summary = "Create a parameter for a campaign")
    @PutMapping(value = UrlConstants.API_CAMPAIGNS_ID_PARAMS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ParamsDto> putParams(@PathVariable("id") String id, @RequestBody @Valid ParamsDto paramsDto) {
        Campaign campaign = campaignService.findById(StringUtils.upperCase(id));
        ParamValidator.validateParams(paramsDto);
        return campaignService.saveParameterForCampaign(campaign, paramsDto);
    }

    @Operation(summary = "Update or create a campaign")
    @PutMapping(value = UrlConstants.API_CAMPAIGNS_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CampaignDto.class))),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = CampaignDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "LUNATIC_NORMAL campaign", value = LUNATIC_NORMAL_CAMPAIGN),
                    @ExampleObject(name = "LUNATIC_SENSITIVE campaign", value = LUNATIC_SENSITIVE_CAMPAIGN),
                    @ExampleObject(name = "ORBEON1 campaign", value = ORBEON1_CAMPAIGN),
                    @ExampleObject(name = "ORBEON2 campaign", value = ORBEON2_CAMPAIGN)
            },
                    schema = @Schema(implementation = CampaignDto.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE))
    public ResponseEntity<CampaignDto> putCampaign(@PathVariable("id") String id, @RequestBody @Valid CampaignDto campaignDto) {
        if (!campaignDto.getId().equalsIgnoreCase(id)) {
            throw new NotMatchException("id and idCampaign don't match");
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(campaignDto.getId()).toUriString());
        HttpStatus httpStatus;

        try {
            campaignService.findById(id);
            log.info("Update campaign with the id {}", campaignDto.getId());
            httpStatus = HttpStatus.OK;
        } catch (NotFoundException e) {
            log.info("Create campaign with the id {}", campaignDto.getId());
            httpStatus = HttpStatus.CREATED;
        }

        Campaign campaign = campaignService.insertOrUpdateCampaign(convertToEntity(campaignDto));
        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(convertToDto(campaign));
    }

    @Operation(summary = "Delete a campaign, its campaigns, partitionings, questionings ...")
    @DeleteMapping(value = {UrlConstants.API_CAMPAIGNS_ID, UrlConstants.MOOG_API_CAMPAIGNS_ID})
    @Transactional
    public void deleteCampaign(@PathVariable("id") String id) throws NotFoundException {
        Campaign campaign = campaignService.findById(id);
        if (campaignService.isCampaignOngoing(id)) {
            throw new ImpossibleToDeleteException("Campaign is still ongoing and can't be deleted");
        }


        int nbQuestioningDeleted = 0;
        List<Upload> uploadsCamp = uploadService.findAllByIdCampaign(id);
        campaignService.deleteCampaignById(id);
        Set<Partitioning> listPartitionings = campaign.getPartitionings();

        int nbViewDeleted = viewService.deleteViewsOfOneCampaign(campaign);

        for (Partitioning partitioning : listPartitionings) {
            nbQuestioningDeleted += questioningService.deleteQuestioningsOfOnePartitioning(partitioning);
        }
        uploadsCamp.forEach(uploadService::delete);
        log.info("Campaign {} deleted with all its metadata children - {} questioning deleted - {} view deleted - {} uploads deleted",
                id,
                nbQuestioningDeleted, nbViewDeleted, uploadsCamp.size());

    }

    @Operation(summary = "campaign is ongoing")
    @GetMapping(value = UrlConstants.CAMPAIGNS_ID_ONGOING, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = OnGoingDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public OnGoingDto isOnGoingCampaign(@PathVariable("id") String id) {
        return new OnGoingDto(campaignService.isCampaignOngoing(id));
    }

    @Operation(summary = "get ongoing campaigns")
    @GetMapping(value = UrlConstants.API_CAMPAIGNS_ONGOING, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CampaignOngoingDto> getOngoingCampaigns() {
        return campaignService.getCampaignOngoingDtos();
    }

    @Operation(summary = "Get commons ongoing campaigns")
    @GetMapping(value = UrlConstants.API_CAMPAIGNS_COMMONS_ONGOING, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CampaignCommonsDto> getCommonsOngoingCampaigns() {
        return campaignService.getCampaignCommonsOngoingDtos();
    }

    @Operation(summary = "Get commons campaign")
    @GetMapping(value = UrlConstants.API_CAMPAIGNS_COMMONS_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CampaignCommonsDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public CampaignCommonsDto getCommonsCampaignsById(@PathVariable("id") String id) {
        return campaignService.findCampaignDtoById(id);
    }


    @Operation(summary = "Search campaigns")
    @GetMapping(value = UrlConstants.API_CAMPAIGNS_SEARCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CampaignSummaryDto.class))))
    })
    public Page<CampaignSummaryDto> searchCampaigns(@RequestParam(required = false) String searchParam,
                                                    @RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("Search campaigns by {} with page = {} pageSize = {}", searchParam, page, pageSize);
        return campaignService.searchCampaigns(searchParam, PageRequest.of(page, pageSize));
    }

    @Operation(summary = "Get campaign header")
    @GetMapping(value = UrlConstants.API_CAMPAIGNS_ID_HEADER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CampaignHeaderDto.class)))
    })
    public CampaignHeaderDto getHeader(@PathVariable("id") String id) {
        log.info("Get campaign header by id {}", id);
        return campaignService.findCampaignHeaderById(id);
    }

    private CampaignDto convertToDto(Campaign campaign) {
        return modelmapper.map(campaign, CampaignDto.class);
    }

    private ParamsDto convertToDto(Parameters params) {
        return modelmapper.map(params, ParamsDto.class);
    }

    private CampaignPartitioningsDto convertToCampaignPartitioningsDto(Campaign campaign) {
        return modelmapper.map(campaign, CampaignPartitioningsDto.class);
    }

    private Campaign convertToEntity(CampaignDto campaignDto) {
        return modelmapper.map(campaignDto, Campaign.class);
    }

    public static class CampaignPage extends PageImpl<CampaignDto> {

        public CampaignPage(List<CampaignDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
