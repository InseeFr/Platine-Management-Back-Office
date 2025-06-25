package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningAccreditationRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MySurveyServiceImplTest {

    private MySurveysServiceImpl mySurveysService;
    private QuestioningAccreditationRepositoryStub questioningAccreditationRepositoryStub;
    private QuestioningServiceStub questioningService;
    @Mock
    private QuestioningUrlComponent questioningUrlComponent;
    private Instant instant;
    private MyQuestionnaireDetailsDto myQuestionnaireDetailsDto;

    @BeforeEach
    void setUp() {
        questioningAccreditationRepositoryStub = new QuestioningAccreditationRepositoryStub();
        Date date = new Date();
        instant = date.toInstant();
        questioningService = new QuestioningServiceStub();
        UUID questioningId = UUID.randomUUID();
        myQuestionnaireDetailsDto = new MyQuestionnaireDetailsDto();
        questioningAccreditationRepositoryStub.setMyQuestionnaireDetailsDto(List.of(myQuestionnaireDetailsDto));

        myQuestionnaireDetailsDto.setSurveyUnitId("SU123");
        myQuestionnaireDetailsDto.setSurveyUnitIdentificationCode("Code123");
        myQuestionnaireDetailsDto.setSurveyUnitIdentificationName("Name123");
        myQuestionnaireDetailsDto.setSourceId("source1");
        myQuestionnaireDetailsDto.setPartitioningLabel("Partition Label");
        myQuestionnaireDetailsDto.setQuestioningId(questioningId);
        myQuestionnaireDetailsDto.setPartitioningId("partition1");
        myQuestionnaireDetailsDto.setPartitioningReturnDate(date);

        mySurveysService = new MySurveysServiceImpl(
                questioningService,
                questioningUrlComponent,
                questioningAccreditationRepositoryStub);

        Questioning mockQuestioning = new Questioning();
        mockQuestioning.setId(questioningId);
        mockQuestioning.setIdPartitioning("partition1");
        questioningService.saveQuestioning(mockQuestioning);

        Partitioning mockPartitioning = new Partitioning();
        mockPartitioning.setLabel("Partition Label");
        mockPartitioning.setId(mockQuestioning.getIdPartitioning());
    }

    @Test
    @DisplayName("Should return questionnaire list when status is IN_PROGRESS")
    void getListMyQuestionnairesTest() {
        when(questioningUrlComponent.buildAccessUrl(any(),any())).thenReturn("http://access-url");
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.IN_PROGRESS);
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.LUNATIC_NORMAL.name());
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.partitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.surveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.surveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.questioningAccessUrl()).isEqualTo("http://access-url");
        assertThat(dto.partitioningId()).isEqualTo("partition1");
        assertThat(dto.surveyUnitId()).isEqualTo("SU123");
        assertThat(dto.questioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.IN_PROGRESS.name());
        assertThat(dto.depositProofUrl()).isNull();
        assertThat(dto.partitioningReturnDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should return questionnaire list when status is INCOMING")
    void getListMyQuestionnairesTest2() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.INCOMING);
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.LUNATIC_NORMAL.name());
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.partitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.surveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.surveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.partitioningId()).isEqualTo("partition1");
        assertThat(dto.surveyUnitId()).isEqualTo("SU123");
        assertThat(dto.questioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.INCOMING.name());
        assertThat(dto.questioningAccessUrl()).isNull();
        assertThat(dto.depositProofUrl()).isNull();
        assertThat(dto.partitioningReturnDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should return questionnaire list when status is RECEIVED with Lunatic")
    void getListMyQuestionnairesTest3() {
        when(questioningUrlComponent.buildDepositProofUrl(any(),any())).thenReturn("http://depositProof-url");
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.LUNATIC_NORMAL.name());

        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.partitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.surveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.surveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.partitioningId()).isEqualTo("partition1");
        assertThat(dto.surveyUnitId()).isEqualTo("SU123");
        assertThat(dto.questioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.RECEIVED.name());
        assertThat(dto.depositProofUrl()).isEqualTo("http://depositProof-url");
        assertThat(dto.questioningAccessUrl()).isNull();
        assertThat(dto.partitioningReturnDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should return questionnaire list when status is RECEIVED with XForm")
    void getListMyQuestionnairesTest4() {
        when(questioningUrlComponent.buildAccessUrl(any(),any())).thenReturn("http://access-url");
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.XFORM1.toString());

        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.questioningAccessUrl()).isEqualTo("http://access-url");
        assertThat(dto.depositProofUrl()).isNull();
    }

    @Test
    @DisplayName("Should return questionnaire list when status is RECEIVED with Lunatic Sensitive")
    void getListMyQuestionnairesTest5() {
        when(questioningUrlComponent.buildDepositProofUrl(any(),any())).thenReturn("http://depositProof-url");
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.LUNATIC_SENSITIVE.toString());

        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.partitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.surveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.surveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.partitioningId()).isEqualTo("partition1");
        assertThat(dto.surveyUnitId()).isEqualTo("SU123");
        assertThat(dto.questioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.RECEIVED.name());
        assertThat(dto.depositProofUrl()).isEqualTo("http://depositProof-url");
        assertThat(dto.questioningAccessUrl()).isNull();
        assertThat(dto.partitioningReturnDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should return empty list when no accreditations are found")
    void getListMyQuestionnairesTest6() {
        questioningAccreditationRepositoryStub.setMyQuestionnaireDetailsDto(List.of());
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("456");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return questionnaire list when status is NOT_STARTED")
    void getListMyQuestionnairesTest7() {
        when(questioningUrlComponent.buildAccessUrl(any(),any())).thenReturn("http://access-url");
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.NOT_STARTED);
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.LUNATIC_NORMAL.name());
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.partitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.surveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.surveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.questioningAccessUrl()).isEqualTo("http://access-url");
        assertThat(dto.partitioningId()).isEqualTo("partition1");
        assertThat(dto.surveyUnitId()).isEqualTo("SU123");
        assertThat(dto.questioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.NOT_STARTED.name());
        assertThat(dto.depositProofUrl()).isNull();
        assertThat(dto.partitioningReturnDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should return questionnaire ofats file upload")
    void getListMyQuestionnairesOfatsFileUpload() {
        when(questioningUrlComponent.buildDownloadUrl(any())).thenReturn("http://download-url");
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.NOT_STARTED);
        Source source = createSource("OFATS");
        Survey survey = createSurvey(source, "OFATS2025", 2025);
        Campaign campaign = createCampaign("OFATSRD2025A00", PeriodEnum.A00, survey, DataCollectionEnum.FILE_UPLOAD, "ofats");
        Partitioning partitioning = createPartitioning("partitioningOfats", campaign);
        SurveyUnit surveyUnit = createSurveyUnit("OFATSRD2025A000001");
        Questioning questioning = createQuestioning(UUID.randomUUID());
        MyQuestionnaireDetailsDto questionnaireDetailsDto = createQuestionnaireDetailsDto(source, survey, campaign, partitioning, surveyUnit, questioning);
        List<MyQuestionnaireDetailsDto> questionnaireDetailsDtoList = List.of(questionnaireDetailsDto);
        questioningAccreditationRepositoryStub.setMyQuestionnaireDetailsDto(questionnaireDetailsDtoList);


        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("1234");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.questioningAccessUrl()).isNull();
        assertThat(dto.depositProofUrl()).isNull();
        assertThat(dto.questioningDownloadFileName()).isNotBlank();
    }

    private Questioning createQuestioning(UUID id) {
        Questioning questioning = new Questioning();
        questioning.setId(id);
        questioning.setModelName("modelName");
        return questioning;
    }

    private SurveyUnit createSurveyUnit(String id) {
        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setIdSu(id);
        surveyUnit.setIdentificationCode("Code123");
        surveyUnit.setIdentificationName("Name123");
        return surveyUnit;
    }

    private MyQuestionnaireDetailsDto createQuestionnaireDetailsDto(Source source, Survey survey, Campaign campaign, Partitioning partitioning, SurveyUnit surveyUnit, Questioning questioning) {
        MyQuestionnaireDetailsDto dto = new MyQuestionnaireDetailsDto();
        dto.setSourceId(source.getId());
        dto.setSurveyYear(survey.getYear());
        dto.setPeriod(campaign.getPeriod().name());
        dto.setPartitioningLabel(partitioning.getLabel());
        dto.setPartitioningId(partitioning.getId());
        dto.setPartitioningOpeningDate(partitioning.getOpeningDate());
        dto.setPartitioningClosingDate(partitioning.getClosingDate());
        dto.setPartitioningReturnDate(partitioning.getReturnDate());
        dto.setSurveyUnitIdentificationCode(surveyUnit.getIdentificationCode());
        dto.setSurveyUnitIdentificationName(surveyUnit.getIdentificationName());
        dto.setOperationUploadReference(campaign.getOperationUploadReference());
        dto.setModelName(questioning.getModelName());
        dto.setSurveyUnitId(surveyUnit.getIdSu());
        dto.setDataCollectionTarget(campaign.getDataCollectionTarget().name());
        dto.setQuestioningId(questioning.getId());
        return dto;
    }

    private Partitioning createPartitioning(String id, Campaign campaign) {
        Partitioning partitioning = new Partitioning();
        partitioning.setId(id);
        partitioning.setLabel("label");
        partitioning.setOpeningDate(new Date());
        partitioning.setClosingDate(new Date());
        partitioning.setReturnDate(new Date());
        partitioning.setCampaign(campaign);
        return partitioning;
    }

    private Source createSource(String id) {
        Source source = new Source();
        source.setId(id);
        return source;
    }

    private Survey createSurvey(Source source, String id, Integer year) {
        Survey survey = new Survey();
        survey.setId(id);
        survey.setYear(year);
        survey.setSource(source);
        return survey;
    }

    private Campaign createCampaign(String id, PeriodEnum period, Survey survey, DataCollectionEnum dataCollection, String operationUploadReference) {
        Campaign campaign = new Campaign();
        campaign.setId(id);
        campaign.setPeriod(period);
        campaign.setSurvey(survey);
        campaign.setDataCollectionTarget(dataCollection);
        campaign.setOperationUploadReference(operationUploadReference);
        return campaign;
    }

}
