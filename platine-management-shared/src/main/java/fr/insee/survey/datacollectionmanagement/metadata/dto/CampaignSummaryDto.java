package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class CampaignSummaryDto {

    private String campaignId;
    private String source;
    private int year;
    private String period;
    private CollectionStatus status;
    private Instant openingDate;
    private Instant closingDate;
}
