package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.query.service.impl.MySurveysServiceImpl;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.PartitioningServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningAccreditationServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningEventServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MySurveyServiceImplTest {

    private MySurveysServiceImpl mySurveysService;
    private QuestioningAccreditationServiceStub questioningAccreditationService;

    QuestioningServiceStub questioningService;
    Questioning mockQuestioning;
    Partitioning mockPartitioning;

    @BeforeEach
    void setUp() {
        PartitioningServiceStub partitioningService;
        SurveyUnit mockSurveyUnit;
        QuestioningEventServiceStub questioningEventServiceStub;
        QuestioningAccreditation mockAccreditation;
        Campaign mockCampaign;
        Survey mockSurvey;
        Source mockSource;

        questioningAccreditationService = new QuestioningAccreditationServiceStub();
        partitioningService = new PartitioningServiceStub();
        questioningEventServiceStub = new QuestioningEventServiceStub();
        questioningService = new QuestioningServiceStub();
        mySurveysService = new MySurveysServiceImpl(questioningAccreditationService, partitioningService, questioningEventServiceStub, questioningService);

        mockSurveyUnit = new SurveyUnit();
        mockSurveyUnit.setIdSu("SU123");
        mockSurveyUnit.setIdentificationCode("Code123");
        mockSurveyUnit.setIdentificationName("Name123");

        mockQuestioning = new Questioning();
        mockQuestioning.setId(1L);
        mockQuestioning.setIdPartitioning("partition1");
        mockQuestioning.setSurveyUnit(mockSurveyUnit);

        mockAccreditation = new QuestioningAccreditation();
        mockAccreditation.setQuestioning(mockQuestioning);

        mockSource = new Source();
        mockSource.setId("source1");

        mockSurvey = new Survey();
        mockSurvey.setSource(mockSource);

        mockCampaign = new Campaign();
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
    void testGetListMyQuestionnairesStatusOpen() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.OPEN);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123", null);

        assertNotNull(result);
        assertEquals(1, result.size());

        MyQuestionnaireDto dto = result.getFirst();

        assertEquals("Partition Label", dto.getPartitioningLabel());
        assertEquals("Code123", dto.getSurveyUnitIdentificationCode());
        assertEquals("Name123", dto.getSurveyUnitIdentificationName());
        assertEquals("http://access-url", dto.getQuestioningAccessUrl());
        assertEquals("partition1", dto.getPartitioningId());
        assertEquals("SU123", dto.getSurveyUnitId());
        assertEquals(QuestionnaireStatusTypeEnum.OPEN.name(), dto.getQuestioningStatus());
        assertNull(dto.getDepositProofUrl());
    }

    @Test
    void testGetListMyQuestionnairesStatusIncomingStatus() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.INCOMING);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123", null);

        assertNotNull(result);
        assertEquals(1, result.size());

        MyQuestionnaireDto dto = result.getFirst();

        assertEquals("Partition Label", dto.getPartitioningLabel());
        assertEquals("Code123", dto.getSurveyUnitIdentificationCode());
        assertEquals("Name123", dto.getSurveyUnitIdentificationName());
        assertEquals("partition1", dto.getPartitioningId());
        assertEquals("SU123", dto.getSurveyUnitId());
        assertEquals(QuestionnaireStatusTypeEnum.INCOMING.name(), dto.getQuestioningStatus());
        assertNull(dto.getQuestioningAccessUrl());
        assertNull(dto.getDepositProofUrl());
    }

    @Test
    void testGetListMyQuestionnairesStatusReceivedStatusNoXform() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123", null);

        assertNotNull(result);
        assertEquals(1, result.size());

        MyQuestionnaireDto dto = result.getFirst();

        assertEquals("Partition Label", dto.getPartitioningLabel());
        assertEquals("Code123", dto.getSurveyUnitIdentificationCode());
        assertEquals("Name123", dto.getSurveyUnitIdentificationName());
        assertEquals("partition1", dto.getPartitioningId());
        assertEquals("SU123", dto.getSurveyUnitId());
        assertEquals(QuestionnaireStatusTypeEnum.RECEIVED.name(), dto.getQuestioningStatus());
        assertEquals("http://preuve-de-depot/" + mockQuestioning.getSurveyUnit().getIdSu(), dto.getDepositProofUrl());
        assertNull(dto.getQuestioningAccessUrl());
    }

    @Test
    void testGetListMyQuestionnairesStatusReceivedStatusWithXform1() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        mockPartitioning.getCampaign().setDataCollectionTarget(DataCollectionEnum.XFORM1);

        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123", "api.test");

        assertNotNull(result);
        assertEquals(1, result.size());

        MyQuestionnaireDto dto = result.getFirst();

        assertEquals("Partition Label", dto.getPartitioningLabel());
        assertEquals("Code123", dto.getSurveyUnitIdentificationCode());
        assertEquals("Name123", dto.getSurveyUnitIdentificationName());
        assertEquals("partition1", dto.getPartitioningId());
        assertEquals("SU123", dto.getSurveyUnitId());

        assertEquals(QuestionnaireStatusTypeEnum.RECEIVED.name(), dto.getQuestioningStatus());
        assertEquals("api.test/api/survey-unit/" + mockQuestioning.getSurveyUnit().getIdSu() + "/deposit-proof", dto.getQuestioningAccessUrl());
        assertNull(dto.getDepositProofUrl());

        mockPartitioning.getCampaign().setDataCollectionTarget(DataCollectionEnum.XFORM2);
        List<MyQuestionnaireDto> result2 = mySurveysService.getListMyQuestionnaires("123", "api.test");
        MyQuestionnaireDto dto2 = result2.getFirst();
        assertEquals("api.test/api/survey-unit/" + mockQuestioning.getSurveyUnit().getIdSu() + "/deposit-proof", dto2.getQuestioningAccessUrl());
        assertNull(dto.getDepositProofUrl());
    }

    @Test
    void testGetListMyQuestionnairesWhenNoAccreditations() {
        questioningAccreditationService.setQuestioningAccreditationList(List.of());

        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("456", null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
