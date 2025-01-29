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

}
