package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import java.util.List;

public record ContextCreateDto(
        SurveyCreateContextDto survey,
        SourceCreateContextDto source,
        CampaignCreateContextDto campaign,
        List<PartitioningCreateContextDto> partitionings
) {
}
