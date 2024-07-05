package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.ImpossibleToDeleteException;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignPartitioningsDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.OnGoingDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Upload;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadService;
import fr.insee.survey.datacollectionmanagement.util.EmailValidatorRegex;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlTypeEnum.values;
import static java.util.stream.Collectors.joining;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
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
    @GetMapping(value = Constants.API_CAMPAIGNS, produces = "application/json")
    public ResponseEntity<CampaignPage> getSources(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Campaign> pageCampaign = campaignService.findAll(pageable);
        List<CampaignDto> listCampaigns = pageCampaign.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(new CampaignPage(listCampaigns, pageable, pageCampaign.getTotalElements()));
    }

    @Operation(summary = "Search for campaigns by the survey id")
    @GetMapping(value = Constants.API_SURVEYS_ID_CAMPAIGNS, produces = "application/json")
    public ResponseEntity<List<CampaignDto>> getCampaignsBySurvey(@PathVariable("id") String id) {

        Survey survey = surveyService.findById(id);
        return ResponseEntity.ok()
                .body(survey.getCampaigns().stream().map(this::convertToDto).toList());

    }

    @Operation(summary = "Search for campaigns and partitionings by the survey id")
    @GetMapping(value = Constants.API_SURVEYS_ID_CAMPAIGNS_PARTITIONINGS, produces = "application/json")
    public ResponseEntity<List<CampaignPartitioningsDto>> getCampaignsPartitioningsBySurvey(@PathVariable("id") String id) {

        Survey survey = surveyService.findById(id);
        return ResponseEntity.ok()
                .body(survey.getCampaigns().stream().map(this::convertToCampaignPartitioningsDto).toList());

    }

    @Operation(summary = "Search for a campaign by its id")
    @GetMapping(value = Constants.API_CAMPAIGNS_ID, produces = "application/json")
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
    @GetMapping(value = "/api/campaigns/{id}/params", produces = "application/json")
    public ResponseEntity<List<ParamsDto>> getParams(@PathVariable("id") String id) {
        Campaign campaign = campaignService.findById(StringUtils.upperCase(id));
        List<ParamsDto> listParams = campaign.getParams().stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(listParams);
    }


    @Operation(summary = "Create a parameter for a campaign")
    @PutMapping(value = "/api/campaigns/{id}/params", produces = "application/json")
    public void putParams(@PathVariable("id") String id, @RequestBody @Valid ParamsDto paramsDto) {
        Campaign campaign = campaignService.findById(StringUtils.upperCase(id));

        if (paramsDto.getParamId().equalsIgnoreCase(Parameters.ParameterEnum.URL_TYPE.name())
                && Arrays.stream(values()).noneMatch(p -> p.name().equals(paramsDto.getParamValue()))) {

            throw new NotMatchException(String.format("Only %s are valid values for URL_TYPE", Arrays.stream(values()).map(item -> item.name())
                    .collect(joining(" "))));
        }
        if (paramsDto.getParamId().equalsIgnoreCase(Parameters.ParameterEnum.MAIL_ASSISTANCE.name())
                && !EmailValidatorRegex.isValidEmail(paramsDto.getParamValue())) {

            throw new NotMatchException(String.format("Email %s is not valid", paramsDto.getParamValue()));
        }
        Parameters param = convertToEntity(paramsDto);
        param.setMetadataId(StringUtils.upperCase(id));
        Set<Parameters> setParams = campaign.getParams().stream()
                .filter(parameter -> !parameter.getParamId().equals(param.getParamId()))
                .collect(Collectors.toSet());
        setParams.add(param);
        campaign.setParams(setParams);
        campaignService.insertOrUpdateCampaign(campaign);
    }


    @Operation(summary = "Update or create a campaign")
    @PutMapping(value = Constants.API_CAMPAIGNS_ID, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CampaignDto.class))),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = CampaignDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
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
        Survey survey = campaign.getSurvey();
        survey.getCampaigns().add(campaign);
        surveyService.insertOrUpdateSurvey(survey);
        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(convertToDto(campaign));
    }

    @Operation(summary = "Delete a campaign, its campaigns, partitionings, questionings ...")
    @DeleteMapping(value = {Constants.API_CAMPAIGNS_ID, Constants.MOOG_API_CAMPAIGNS_ID})

    @Transactional
    public void deleteCampaign(@PathVariable("id") String id) throws fr.insee.survey.datacollectionmanagement.exception.NotFoundException {

        if (campaignService.isCampaignOngoing(id)) {
            throw new ImpossibleToDeleteException("Campaign is still ongoing and can't be deleted");
        }

        Campaign campaign = campaignService.findById(id);

        int nbQuestioningDeleted = 0;
        Survey survey = campaign.getSurvey();
        survey.getCampaigns().remove(campaign);
        surveyService.insertOrUpdateSurvey(survey);
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
    @GetMapping(value = Constants.CAMPAIGNS_ID_ONGOING, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = OnGoingDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<OnGoingDto> isOnGoingCampaign(@PathVariable("id") String id) {
        boolean isOnGoing = campaignService.isCampaignOngoing(id);
        return ResponseEntity.ok().body(new OnGoingDto(isOnGoing));

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

    private Parameters convertToEntity(ParamsDto paramsDto) {

        Parameters params = modelmapper.map(paramsDto, Parameters.class);
        params.setParamId(Parameters.ParameterEnum.valueOf(paramsDto.getParamId()));
        return params;
    }

    class CampaignPage extends PageImpl<CampaignDto> {

        public CampaignPage(List<CampaignDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
