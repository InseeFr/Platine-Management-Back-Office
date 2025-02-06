package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;
import lombok.Data;

@Data
public class CampaignHeaderDto {
    private String campaignId;
    private String source;
    private String period;
    private int year;
    private String wording;
    private CollectionStatus status;
}
