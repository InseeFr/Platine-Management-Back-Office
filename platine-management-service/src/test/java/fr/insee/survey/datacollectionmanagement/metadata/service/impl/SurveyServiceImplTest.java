package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.SurveyRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyServiceImplTest {
    private SurveyServiceImpl surveyService;
    private SurveyRepositoryStub surveyRepositoryStub;

    @BeforeEach
    void init() {
        surveyRepositoryStub = new SurveyRepositoryStub();
        surveyService = new SurveyServiceImpl(surveyRepositoryStub);
    }

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
    @DisplayName("Should add campaign to survey")
    void addCampaignToSurvey_should_add_campaign_to_survey() {
        // given
        String campaign1Id = "campaign-id1";
        Campaign campaign1 = new Campaign();
        campaign1.setId(campaign1Id);

        String campaign2Id = "campaign-id2";
        Campaign campaign2 = new Campaign();
        campaign2.setId(campaign2Id);

        String campaignToAddId = "campaign-id3";
        Campaign campaignToAdd = new Campaign();
        campaignToAdd.setId(campaignToAddId);

        String surveyId = "survey-id";
        Survey surveyInRepo = new Survey();
        surveyInRepo.setId(surveyId);
        HashSet<Campaign> campaignsInRepo = new HashSet<>();
        campaignsInRepo.add(campaign1);
        campaignsInRepo.add(campaign2);
        surveyInRepo.setCampaigns(campaignsInRepo);

        surveyRepositoryStub.setSurveys(List.of(surveyInRepo));

        Survey survey = new Survey();
        survey.setId(surveyId);

        // when
        Survey surveyResult = surveyService.addCampaignToSurvey(survey, campaignToAdd);

        // then
        assertThat(surveyResult).isNotNull();
        assertThat(surveyResult.getCampaigns())
                .isNotNull()
                .hasSize(3)
                .containsExactlyInAnyOrder(campaign1, campaign2, campaignToAdd);
    }

    @Test
    @DisplayName("Should not add campaign if campaign already exist in survey")
    void addCampaignToSurvey_should_not_add_campaign_if_campaign_already_exist_in_survey() {
        // given
        String campaign1Id = "campaign-id1";
        Campaign campaign1 = new Campaign();
        campaign1.setId(campaign1Id);

        String surveyId = "survey-id";
        Survey surveyInRepo = new Survey();
        surveyInRepo.setId(surveyId);
        HashSet<Campaign> campaignsInRepo = new HashSet<>();
        campaignsInRepo.add(campaign1);
        surveyInRepo.setCampaigns(campaignsInRepo);

        surveyRepositoryStub.setSurveys(List.of(surveyInRepo));

        Survey survey = new Survey();
        survey.setId(surveyId);

        // when
        Survey surveyResult = surveyService.addCampaignToSurvey(survey, campaign1);

        // then
        assertThat(surveyResult).isNotNull();
        assertThat(surveyResult.getCampaigns())
                .isNotNull()
                .hasSize(1)
                .first()
                .extracting(Campaign::getId)
                .isEqualTo(campaign1Id);
    }

    @Test
    @DisplayName("Should add campaign list if survey unit in db does not exist")
    void addCampaignToSurvey_should_create_campaign_list_with_campaign_if_empty_campaign_for_survey() {
        // given
        String campaignToAddId = "campaign-id";
        Campaign campaignToAdd = new Campaign();
        campaignToAdd.setId(campaignToAddId);

        String surveyId = "survey-id";
        Survey survey = new Survey();
        survey.setId(surveyId);
        survey.setCampaigns(new HashSet<>());

        // when
        Survey surveyResult = surveyService.addCampaignToSurvey(survey, campaignToAdd);

        // then
        assertThat(surveyResult).isNotNull();
        assertThat(surveyResult.getCampaigns())
                .isNotNull()
                .hasSize(1)
                .first()
                .extracting(Campaign::getId)
                .isEqualTo(campaignToAdd.getId());
    }
}
