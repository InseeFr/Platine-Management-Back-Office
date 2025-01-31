package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;
import lombok.Data;

import java.util.Date;

@Data
public class CampaignSummaryDto {

    private String campaignId;
    private String source;
    private int year;
    private String period;
    private CollectionStatus status;
    private Date openingDate;
    private Date closingDate;
}
