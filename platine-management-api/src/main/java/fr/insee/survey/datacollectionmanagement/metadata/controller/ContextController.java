package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.modelefiliere.CollectionBatchDto;
import fr.insee.modelefiliere.ContextDto;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.CampaignCreateDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.PartitioningCreateDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.SourceCreateDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.SurveyCreateDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ContextController {

    private final CampaignService campaignService;
    private final SurveyService surveyService;
    private final PartitioningService partitioningService;
    private final SourceService sourceService;

    private final ModelMapper modelmapper;

    @Operation(summary = "Post global metadata context")
    @PostMapping(value = "/api/context", produces = "application/json")
    public ResponseEntity<List<PartitioningCreateDto>> postContext(@RequestBody @Valid ContextDto contextDto){

        SourceCreateDto source = convertToSourceCreateDto(contextDto);
        source.setMandatoryMySurveys(false);
        SurveyCreateDto survey = convertToSurveyCreateDto(contextDto);
        CampaignCreateDto campaign = convertToCampaignCreateDto(contextDto);
        List<PartitioningCreateDto> partitionings = contextDto.getCollectionBatchs().stream().map(this::convertToPartitioningCreateDto).toList();
        partitionings.forEach(p ->
            p.setCampaignId(contextDto.getShortLabel()));

        sourceService.insertOrUpdateSource(modelmapper.map(source, Source.class));
        surveyService.insertOrUpdateSurvey(modelmapper.map(survey, Survey.class));
        campaignService.insertOrUpdateCampaign(modelmapper.map(campaign, Campaign.class));
        partitionings.forEach(p -> partitioningService.insertOrUpdatePartitioning(modelmapper.map(p, Partitioning.class)));



        return ResponseEntity.ok().body(contextDto.getCollectionBatchs().stream().map(this::convertToPartitioningCreateDto).toList());
    }

    private SourceCreateDto convertToSourceCreateDto(@Valid ContextDto contextDto) {
        ModelMapper sourceMapper = new ModelMapper();
        TypeMap<ContextDto, SourceCreateDto> propertyMapper = sourceMapper.createTypeMap(ContextDto.class, SourceCreateDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieShortLabel(), SourceCreateDto::setId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieId(), SourceCreateDto::setTechnicalId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieShortLabel(), SourceCreateDto::setShortWording);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieLabel(), SourceCreateDto::setLongWording);
                    mapper.map(src -> src.getMetadatas().getPeriodicity(), SourceCreateDto::setPeriodicity);
                }
        );
        return sourceMapper.map(contextDto, SourceCreateDto.class);
    }
    private SurveyCreateDto convertToSurveyCreateDto(@Valid ContextDto contextDto) {
        ModelMapper surveyMapper = new ModelMapper();
        TypeMap<ContextDto, SurveyCreateDto> propertyMapper = surveyMapper.createTypeMap(ContextDto.class, SurveyCreateDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationShortLabel(), SurveyCreateDto::setId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationId(), SurveyCreateDto::setTechnicalId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationShortLabel(), SurveyCreateDto::setShortWording);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationLabel(), SurveyCreateDto::setLongWording);
                    mapper.map(src -> src.getMetadatas().getYear(), SurveyCreateDto::setYear);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieShortLabel(), SurveyCreateDto::setSourceId);

                    mapper.map(src -> src.getMetadatas().getShortObjectives(), SurveyCreateDto::setShortObjectives);
                    mapper.map(src -> src.getMetadatas().getVisaNumber(), SurveyCreateDto::setVisaNumber);

                }
        );
        return surveyMapper.map(contextDto, SurveyCreateDto.class);
    }
    private CampaignCreateDto convertToCampaignCreateDto(@Valid ContextDto contextDto) {
        ModelMapper campaignMapper = new ModelMapper();
        TypeMap<ContextDto, CampaignCreateDto> propertyMapper = campaignMapper.createTypeMap(ContextDto.class, CampaignCreateDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(ContextDto::getShortLabel, CampaignCreateDto::setId);
                    mapper.map(ContextDto::getId, CampaignCreateDto::setTechnicalId);
                    mapper.map(ContextDto::getLabel, CampaignCreateDto::setCampaignWording);
                    mapper.map(src -> src.getMetadatas().getPeriod(), CampaignCreateDto::setPeriod);
                    mapper.map(src -> src.getMetadatas().getYear(), CampaignCreateDto::setYear);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationShortLabel(), CampaignCreateDto::setSurveyId);
                }
        );
        return campaignMapper.map(contextDto, CampaignCreateDto.class);
    }
    private PartitioningCreateDto convertToPartitioningCreateDto(@Valid CollectionBatchDto collectionBatchDto) {
        ModelMapper partitioningMapper = new ModelMapper();

        TypeMap<CollectionBatchDto, PartitioningCreateDto> propertyMapper = partitioningMapper.createTypeMap(CollectionBatchDto.class, PartitioningCreateDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(CollectionBatchDto::getCollectionBatchShortLabel, PartitioningCreateDto::setId);
                    mapper.map(CollectionBatchDto::getCollectionBatchId, PartitioningCreateDto::setTechnicalId);
                    mapper.map(CollectionBatchDto::getCollectionBatchLabel, PartitioningCreateDto::setLabel);
                    mapper.map(CollectionBatchDto::getManagementEndDate, PartitioningCreateDto::setClosingDate);
                    mapper.map(CollectionBatchDto::getManagementStartDate, PartitioningCreateDto::setOpeningDate);
                }
        );
        return partitioningMapper.map(collectionBatchDto, PartitioningCreateDto.class);
    }
}

