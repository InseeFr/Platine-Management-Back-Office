package fr.insee.survey.datacollectionmanagement.metadata.documentation.examples;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CampaignDocumentation {

    public static final String LUNATIC_NORMAL_CAMPAIGN = """
            {
              "id": "string",
              "surveyId": "string",
              "year": 2025,
              "campaignWording": "string",
              "period": "string",
              "sensitivity": false,
              "dataCollectionTarget": "LUNATIC_NORMAL"
            }""";

    public static final String ORBEON1_CAMPAIGN = """
            {
              "id": "string",
              "surveyId": "string",
              "year": 2025,
              "campaignWording": "string",
              "period": "string",
              "sensitivity": false,
              "dataCollectionTarget": "XFORM1"
            }""";

    public static final String ORBEON2_CAMPAIGN = """
            {
              "id": "string",
              "surveyId": "string",
              "year": 2025,
              "campaignWording": "string",
              "period": "string",
              "sensitivity": false,
              "dataCollectionTarget": "XFORM2"
            }""";


    public static final String LUNATIC_SENSITIVE_CAMPAIGN = """
            {
              "id": "string",
              "surveyId": "string",
              "year": 1073741824,
              "campaignWording": "string",
              "period": "string",
              "sensitivity": true,
              "dataCollectionTarget": "LUNATIC_SENSITIVE"
            }""";
}
