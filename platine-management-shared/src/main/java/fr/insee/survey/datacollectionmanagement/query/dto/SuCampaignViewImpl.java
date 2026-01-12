package fr.insee.survey.datacollectionmanagement.query.dto;

public class SuCampaignViewImpl implements SuCampaignView {
        private final String surveyUnitIdSu;
        private final String campaignId;

        public SuCampaignViewImpl(String surveyUnitIdSu, String campaignId) {
            this.surveyUnitIdSu = surveyUnitIdSu;
            this.campaignId = campaignId;
        }

        @Override public String getSurveyUnitIdSu() { return surveyUnitIdSu; }
        @Override public String getCampaignId() { return campaignId; }
    }
