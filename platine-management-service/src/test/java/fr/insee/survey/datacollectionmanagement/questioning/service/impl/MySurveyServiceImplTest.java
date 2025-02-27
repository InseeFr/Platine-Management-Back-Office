package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
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

    @BeforeEach
    void setUp() {
        PartitioningServiceStub partitioningService;
        QuestioningServiceStub questioningService;
        QuestioningEventServiceStub questioningEventServiceStub;
        QuestioningAccreditation mockAccreditation;
        Partitioning mockPartitioning;
        Questioning mockQuestioning;
        SurveyUnit mockSurveyUnit;
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
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.OPEN);
    }

    @Test
    void testGetListMyQuestionnaires() {
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertNotNull(result);
        assertEquals(1, result.size());

        MyQuestionnaireDto dto = result.getFirst();

        assertEquals("Partition Label", dto.getPartitioningLabel());
        assertEquals("Code123", dto.getSurveyUnitIdentificationCode());
        assertEquals("Name123", dto.getSurveyUnitIdentificationName());
        assertEquals("http://access-url", dto.getQuestioningAccessUrl());
        assertEquals("http://preuve-de-depot/SU123", dto.getDeliveryUrl());
        assertEquals("partition1", dto.getPartitioningId());
        assertEquals("SU123", dto.getSurveyUnitId());
        assertEquals(QuestionnaireStatusTypeEnum.OPEN.name(), dto.getQuestioningStatus());
    }

    @Test
    void testGetListMyQuestionnairesWhenNoAccreditations() {
        questioningAccreditationService.setQuestioningAccreditationList(List.of());

        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("456");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
