package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.PartitioningDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SourceDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SurveyDto;
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
    public ResponseEntity<List<PartitioningDto>> postContext(@RequestBody @Valid ContextDto contextDto){

        SourceDto source = convertToSourceDto(contextDto);
        source.setMandatoryMySurveys(false);
        SurveyDto survey = convertToSurveyDto(contextDto);
        CampaignDto campaign = convertToCampaignDto(contextDto);
        List<PartitioningDto> partitionings = contextDto.getCollectionBatchs().stream().map(this::convertToPartitioningDto).toList();
        partitionings.forEach(p ->
        {
            p.setCampaignId(contextDto.getId().toString());
            p.setCampaignShortWording(contextDto.getShortLabel());
        });

        sourceService.insertOrUpdateSource(modelmapper.map(source, Source.class));
        surveyService.insertOrUpdateSurvey(modelmapper.map(survey, Survey.class));
        campaignService.insertOrUpdateCampaign(modelmapper.map(campaign, Campaign.class));
        partitionings.forEach(p -> partitioningService.insertOrUpdatePartitioning(modelmapper.map(p, Partitioning.class)));



        return ResponseEntity.ok().body(contextDto.getCollectionBatchs().stream().map(this::convertToPartitioningDto).toList());
    }

    private SourceDto convertToSourceDto(@Valid ContextDto contextDto) {
        ModelMapper sourceMapper = new ModelMapper();
        TypeMap<ContextDto, SourceDto> propertyMapper = sourceMapper.createTypeMap(ContextDto.class, SourceDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieId(), SourceDto::setId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieShortLabel(), SourceDto::setShortWording);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieLabel(), SourceDto::setLongWording);
                    mapper.map(src -> src.getMetadatas().getPeriodicity(), SourceDto::setPeriodicity);
                }
        );
        return sourceMapper.map(contextDto, SourceDto.class);
    }
    private SurveyDto convertToSurveyDto(@Valid ContextDto contextDto) {
        ModelMapper surveyMapper = new ModelMapper();
        TypeMap<ContextDto, SurveyDto> propertyMapper = surveyMapper.createTypeMap(ContextDto.class, SurveyDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationId(), SurveyDto::setId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationShortLabel(), SurveyDto::setShortWording);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationLabel(), SurveyDto::setLongWording);
                    mapper.map(src -> src.getMetadatas().getYear(), SurveyDto::setYear);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieId(), SurveyDto::setSourceId);

                    mapper.map(src -> src.getMetadatas().getShortObjectives(), SurveyDto::setShortObjectives);
                    mapper.map(src -> src.getMetadatas().getVisaNumber(), SurveyDto::setVisaNumber);

                }
        );
        return surveyMapper.map(contextDto, SurveyDto.class);
    }
    private CampaignDto convertToCampaignDto(@Valid ContextDto contextDto) {
        ModelMapper campaignMapper = new ModelMapper();
        TypeMap<ContextDto, CampaignDto> propertyMapper = campaignMapper.createTypeMap(ContextDto.class, CampaignDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(ContextDto::getId, CampaignDto::setId);
                    mapper.map(ContextDto::getLabel, CampaignDto::setCampaignWording);
                    mapper.map(ContextDto::getShortLabel, CampaignDto::setShortWording);
                    mapper.map(src -> src.getMetadatas().getPeriod(), CampaignDto::setPeriod);
                    mapper.map(src -> src.getMetadatas().getYear(), CampaignDto::setYear);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationId(), CampaignDto::setSurveyId);
                }
        );
        return campaignMapper.map(contextDto, CampaignDto.class);
    }
    private PartitioningDto convertToPartitioningDto(@Valid CollectionBatchDto collectionBatchDto) {
        ModelMapper partitioningMapper = new ModelMapper();

        TypeMap<CollectionBatchDto, PartitioningDto> propertyMapper = partitioningMapper.createTypeMap(CollectionBatchDto.class, PartitioningDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(CollectionBatchDto::getCollectionBatchId, PartitioningDto::setId);
                    mapper.map(CollectionBatchDto::getCollectionBatchLabel, PartitioningDto::setLabel);
                    mapper.map(CollectionBatchDto::getCollectionBatchShortLabel, PartitioningDto::setShortWording);
                    mapper.map(CollectionBatchDto::getManagementEndDate, PartitioningDto::setClosingDate);
                    mapper.map(CollectionBatchDto::getManagementStartDate, PartitioningDto::setOpeningDate);
                }
        );
        return partitioningMapper.map(collectionBatchDto, PartitioningDto.class);
    }
}

