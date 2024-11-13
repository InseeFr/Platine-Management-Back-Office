package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoogCampaignDto {
    private String id;
    private String label;
    private Long collectionStartDate;
    private Long collectionEndDate;

}
