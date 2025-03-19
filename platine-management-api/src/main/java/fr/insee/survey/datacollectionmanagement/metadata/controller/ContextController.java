package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.modelefiliere.CollectionBatchDto;
import fr.insee.modelefiliere.ContextDto;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.CampaignCreateContextDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.PartitioningCreateContextDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.SourceCreateContextDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.SurveyCreateContextDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
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
    public ResponseEntity<List<PartitioningCreateContextDto>> postContext(@RequestBody @Valid ContextDto contextDto){

        SourceCreateContextDto source = convertToSourceCreateContextDto(contextDto);
        source.setMandatoryMySurveys(false);
        SurveyCreateContextDto survey = convertToSurveyCreateContextDto(contextDto);
        CampaignCreateContextDto campaign = convertToCampaignCreateContextDto(contextDto);
        List<PartitioningCreateContextDto> partitionings = contextDto.getCollectionBatchs().stream().map(this::convertToPartitioningCreateContextDto).toList();
        partitionings.forEach(p ->
            p.setCampaignId(contextDto.getShortLabel()));

        sourceService.insertOrUpdateSource(modelmapper.map(source, Source.class));
        surveyService.insertOrUpdateSurvey(modelmapper.map(survey, Survey.class));
        campaignService.insertOrUpdateCampaign(modelmapper.map(campaign, Campaign.class));
        partitionings.forEach(p -> partitioningService.insertOrUpdatePartitioning(modelmapper.map(p, Partitioning.class)));



        return ResponseEntity.ok().body(contextDto.getCollectionBatchs().stream().map(this::convertToPartitioningCreateContextDto).toList());
    }

    private SourceCreateContextDto convertToSourceCreateContextDto(@Valid ContextDto contextDto) {
        ModelMapper sourceMapper = new ModelMapper();
        TypeMap<ContextDto, SourceCreateContextDto> propertyMapper = sourceMapper.createTypeMap(ContextDto.class, SourceCreateContextDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieShortLabel(), SourceCreateContextDto::setId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieId(), SourceCreateContextDto::setTechnicalId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieShortLabel(), SourceCreateContextDto::setShortWording);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieLabel(), SourceCreateContextDto::setLongWording);
                    mapper.map(src -> src.getMetadatas().getPeriodicity(), SourceCreateContextDto::setPeriodicity);
                }
        );
        return sourceMapper.map(contextDto, SourceCreateContextDto.class);
    }
    private SurveyCreateContextDto convertToSurveyCreateContextDto(@Valid ContextDto contextDto) {
        ModelMapper surveyMapper = new ModelMapper();
        TypeMap<ContextDto, SurveyCreateContextDto> propertyMapper = surveyMapper.createTypeMap(ContextDto.class, SurveyCreateContextDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationShortLabel(), SurveyCreateContextDto::setId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationId(), SurveyCreateContextDto::setTechnicalId);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationShortLabel(), SurveyCreateContextDto::setShortWording);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationLabel(), SurveyCreateContextDto::setLongWording);
                    mapper.map(src -> src.getMetadatas().getYear(), SurveyCreateContextDto::setYear);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationSerieShortLabel(), SurveyCreateContextDto::setSourceId);

                    mapper.map(src -> src.getMetadatas().getShortObjectives(), SurveyCreateContextDto::setShortObjectives);
                    mapper.map(src -> src.getMetadatas().getVisaNumber(), SurveyCreateContextDto::setVisaNumber);
                    mapper.map(src -> src.getMetadatas().getCompulsoryNature(), SurveyCreateContextDto::setCompulsoryNature);


                }
        );
        return surveyMapper.map(contextDto, SurveyCreateContextDto.class);
    }
    private CampaignCreateContextDto convertToCampaignCreateContextDto(@Valid ContextDto contextDto) {
        ModelMapper campaignMapper = new ModelMapper();
        TypeMap<ContextDto, CampaignCreateContextDto> propertyMapper = campaignMapper.createTypeMap(ContextDto.class, CampaignCreateContextDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(ContextDto::getShortLabel, CampaignCreateContextDto::setId);
                    mapper.map(ContextDto::getId, CampaignCreateContextDto::setTechnicalId);
                    mapper.map(ContextDto::getLabel, CampaignCreateContextDto::setCampaignWording);
                    mapper.map(src -> src.getMetadatas().getPeriod(), CampaignCreateContextDto::setPeriod);
                    mapper.map(src -> src.getMetadatas().getYear(), CampaignCreateContextDto::setYear);
                    mapper.map(src -> src.getMetadatas().getStatisticalOperationShortLabel(), CampaignCreateContextDto::setSurveyId);
                }
        );
        return campaignMapper.map(contextDto, CampaignCreateContextDto.class);
    }
    private PartitioningCreateContextDto convertToPartitioningCreateContextDto(@Valid CollectionBatchDto collectionBatchDto) {
        ModelMapper partitioningMapper = new ModelMapper();
        // Custom converter to map Instant to Date
        Converter<Instant, Date> instantToDateConverter = context -> {
            Instant source = context.getSource();
            return (source != null) ? Date.from(source) : null;
        };
        TypeMap<CollectionBatchDto, PartitioningCreateContextDto> propertyMapper = partitioningMapper.createTypeMap(CollectionBatchDto.class, PartitioningCreateContextDto.class);
        propertyMapper.addMappings(
                mapper ->
                {
                    mapper.map(CollectionBatchDto::getCollectionBatchShortLabel, PartitioningCreateContextDto::setId);
                    mapper.map(CollectionBatchDto::getCollectionBatchId, PartitioningCreateContextDto::setTechnicalId);
                    mapper.map(CollectionBatchDto::getCollectionBatchLabel, PartitioningCreateContextDto::setLabel);
                    // Use the custom converter for Instant to Date conversion
                    mapper.using(instantToDateConverter).map(CollectionBatchDto::getCollectionEndDate, PartitioningCreateContextDto::setClosingDate);
                    // Use the custom converter for Instant to Date conversion
                    mapper.using(instantToDateConverter).map(CollectionBatchDto::getCollectionStartDate, PartitioningCreateContextDto::setOpeningDate);
                }
        );
        return partitioningMapper.map(collectionBatchDto, PartitioningCreateContextDto.class);
    }

}

