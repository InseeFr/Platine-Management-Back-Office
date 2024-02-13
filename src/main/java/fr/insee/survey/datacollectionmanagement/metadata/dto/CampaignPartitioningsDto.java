package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CampaignPartitioningsDto {

    @NotBlank
    private String id;
    private String surveyId;
    private int year;
    private String campaignWording;
    private PeriodEnum period;
    private List<PartitioningDto> partitionings;
}
