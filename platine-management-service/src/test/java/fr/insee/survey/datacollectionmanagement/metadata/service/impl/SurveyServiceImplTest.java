package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.SurveyRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignServiceStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SurveyServiceImplTest {

    private final SurveyRepositoryStub surveyRepositoryStub = new SurveyRepositoryStub();
    private final CampaignServiceStub campaignServiceStub= new CampaignServiceStub();
    private final SurveyServiceImpl surveyService = new SurveyServiceImpl(surveyRepositoryStub, campaignServiceStub);

    @Test
    @DisplayName("Should return survey")
    void findById_should_return_survey() {
        // given
        String surveyId = "survey-id";
        Survey survey = new Survey();
        survey.setId(surveyId);
        surveyRepositoryStub.setSurveys(List.of(survey));

        // when
        Survey surveyResult = surveyService.findById(surveyId);

        // then
        assertThat(surveyResult).isNotNull();
        assertThat(surveyResult.getId()).isEqualTo(surveyId);
    }

    @Test
    @DisplayName("Should throw exception")
    void findById_should_throw_exception() {
        // given
        String surveyId = "survey-id";
        Survey survey = new Survey();
        survey.setId(surveyId);
        surveyRepositoryStub.setSurveys(List.of(survey));

        // when & then
        assertThatThrownBy(() -> surveyService.findById("not-exist-id"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Should insert survey")
    void insertOrUpdateSupport_should_insert_survey() {
        // given
        String surveyId = "survey-id";
        Survey surveyToInsert = new Survey();
        surveyToInsert.setId(surveyId);

        // when
        Survey surveyResult = surveyService.insertOrUpdateSurvey(surveyToInsert);

        // then
        assertThat(surveyResult).isNotNull();
        assertThat(surveyResult.getCampaigns()).isNull();
    }

    @Test
    @DisplayName("Should update survey")
    void insertOrUpdateSupport_should_update_survey() {
        // given
        String campaignId = "campaign-id";
        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
        String surveyId = "survey-id";
        Survey surveyInRepo = new Survey();
        surveyInRepo.setId(surveyId);
        surveyInRepo.setCampaigns(Set.of(campaign));
        surveyRepositoryStub.setSurveys(List.of(surveyInRepo));

        Survey surveyToUpdate = new Survey();
        surveyToUpdate.setId(surveyId);

        // when
        Survey surveyResult = surveyService.insertOrUpdateSurvey(surveyToUpdate);

        // then
        assertThat(surveyResult).isNotNull();
        assertThat(surveyResult.getCampaigns())
                .isNotNull()
                .hasSize(1)
                .first()
                .extracting(Campaign::getId)
                .isEqualTo(campaignId);
    }

    @Test
    @DisplayName("Should return false when survey does not exist")
    void testIsSurveyOngoing_whenSurveyNotFound() {
        boolean result = surveyService.isSurveyOngoing("unknownSurvey");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when survey has no campaigns")
    void testIsSurveyOngoing_whenSurveyHasNoCampaigns() {
        Survey survey = new Survey();
        survey.setId("MMM2025");
        surveyRepositoryStub.setSurveys(List.of(survey));

        boolean result = surveyService.isSurveyOngoing("MMM2025");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when none of the survey's campaigns are ongoing")
    void testIsSurveyOngoing_whenNoCampaignIsOngoing() {
        Survey survey = new Survey();
        survey.setId("MMM2025");
        Campaign campaign = new Campaign();
        campaign.setId("MMM2025");
        survey.setCampaigns(Set.of(campaign));
        surveyRepositoryStub.setSurveys(List.of(survey));

        boolean result = surveyService.isSurveyOngoing("MMM2025");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when at least one campaign is ongoing")
    void testIsSurveyOngoing_whenAtLeastOneCampaignIsOngoing() {
        Survey survey = new Survey();
        survey.setId("MMM2025");
        Campaign campaign = new Campaign();
        campaign.setId("MMM2025");
        Campaign campaign2 = new Campaign();
        campaign2.setId("ONGOING");
        survey.setCampaigns(Set.of(campaign, campaign2));
        surveyRepositoryStub.setSurveys(List.of(survey));

        boolean result = surveyService.isSurveyOngoing("MMM2025");

        assertTrue(result);
    }

}
