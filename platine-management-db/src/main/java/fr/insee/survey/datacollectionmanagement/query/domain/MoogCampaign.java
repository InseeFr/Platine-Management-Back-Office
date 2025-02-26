package fr.insee.survey.datacollectionmanagement.query.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class MoogCampaign {

    private UUID id;
    private String shortWording;
    private String label;
    private Long collectionStartDate;
    private Long collectionEndDate;

}
