package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.query.service.impl.MySurveysServiceImpl;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class MySurveyServiceImplTest {

    private MySurveysServiceImpl mySurveysService;
    private QuestioningAccreditationServiceStub questioningAccreditationService;
    private QuestioningServiceStub questioningService;
    private Questioning mockQuestioning;
    private Partitioning mockPartitioning;

    @BeforeEach
    void setUp() {
        PartitioningServiceStub partitioningService = new PartitioningServiceStub();
        QuestioningEventServiceStub questioningEventServiceStub = new QuestioningEventServiceStub();
        questioningAccreditationService = new QuestioningAccreditationServiceStub();
        questioningService = new QuestioningServiceStub();

        mySurveysService = new MySurveysServiceImpl(questioningAccreditationService, partitioningService, questioningEventServiceStub, questioningService);

        SurveyUnit mockSurveyUnit = new SurveyUnit();
        mockSurveyUnit.setIdSu("SU123");
        mockSurveyUnit.setIdentificationCode("Code123");
        mockSurveyUnit.setIdentificationName("Name123");

        mockQuestioning = new Questioning();
        mockQuestioning.setId(1L);
        mockQuestioning.setIdPartitioning("partition1");
        mockQuestioning.setSurveyUnit(mockSurveyUnit);

        QuestioningAccreditation mockAccreditation = new QuestioningAccreditation();
        mockAccreditation.setQuestioning(mockQuestioning);

        Source mockSource = new Source();
        mockSource.setId("source1");

        Survey mockSurvey = new Survey();
        mockSurvey.setSource(mockSource);

        Campaign mockCampaign = new Campaign();
        mockCampaign.setSurvey(mockSurvey);

        mockPartitioning = new Partitioning();
        mockPartitioning.setLabel("Partition Label");
        mockPartitioning.setId(mockQuestioning.getIdPartitioning());
        mockPartitioning.setCampaign(mockCampaign);

        questioningAccreditationService.setQuestioningAccreditationList(List.of(mockAccreditation));
        partitioningService.setPartitioning(mockPartitioning);
        questioningService.setAccesUrl("http://access-url");
    }

    @Test
    @DisplayName("Should return questionnaire list when status is OPEN")
    void getListMyQuestionnairesTest() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.OPEN);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123", null);

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.getPartitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.getSurveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.getSurveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.getQuestioningAccessUrl()).isEqualTo("http://access-url");
        assertThat(dto.getPartitioningId()).isEqualTo("partition1");
        assertThat(dto.getSurveyUnitId()).isEqualTo("SU123");
        assertThat(dto.getQuestioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.OPEN.name());
        assertThat(dto.getDepositProofUrl()).isNull();
    }

    @Test
    @DisplayName("Should return questionnaire list when status is INCOMING")
    void getListMyQuestionnairesTest2() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.INCOMING);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123", null);

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.getPartitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.getSurveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.getSurveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.getPartitioningId()).isEqualTo("partition1");
        assertThat(dto.getSurveyUnitId()).isEqualTo("SU123");
        assertThat(dto.getQuestioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.INCOMING.name());
        assertThat(dto.getQuestioningAccessUrl()).isNull();
        assertThat(dto.getDepositProofUrl()).isNull();
    }

    @Test
    @DisplayName("Should return questionnaire list when status is RECEIVED without XForm")
    void getListMyQuestionnairesTest3() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123", null);

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.getPartitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.getSurveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.getSurveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.getPartitioningId()).isEqualTo("partition1");
        assertThat(dto.getSurveyUnitId()).isEqualTo("SU123");
        assertThat(dto.getQuestioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.RECEIVED.name());
        assertThat(dto.getDepositProofUrl()).isEqualTo("http://preuve-de-depot/" + mockQuestioning.getSurveyUnit().getIdSu());
        assertThat(dto.getQuestioningAccessUrl()).isNull();
    }

    @Test
    @DisplayName("Should return questionnaire list when status is RECEIVED with XForm")
    void getListMyQuestionnairesTest4() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        mockPartitioning.getCampaign().setDataCollectionTarget(DataCollectionEnum.XFORM1);

        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123", "api.test");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.getQuestioningAccessUrl()).isEqualTo("api.test/api/survey-unit/" + mockQuestioning.getSurveyUnit().getIdSu() + "/deposit-proof");
        assertThat(dto.getDepositProofUrl()).isNull();
    }

    @Test
    @DisplayName("Should return empty list when no accreditations are found")
    void getListMyQuestionnairesTest5() {
        questioningAccreditationService.setQuestioningAccreditationList(List.of());

        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("456", null);

        assertThat(result).isEmpty();
    }
}
