package fr.insee.survey.datacollectionmanagement.questioning.util;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UrlParameters {
    private String baseUrl;
    private String role;
    private Questioning questioning;
    private String surveyUnitId;
    private String sourceId;
    private String campaignName;

}
