package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.query.service.impl.MySurveysServiceImpl;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MySurveyServiceImplTest {

    private MySurveysServiceImpl mySurveysService;
    private QuestioningAccreditationRepositoryStub questioningAccreditationRepositoryStub;
    private QuestioningServiceStub questioningService;
    @Mock
    private QuestioningUrlComponent questioningUrlComponent;
    private String questionnaireApiUrl;
    private String questionnaireApiUrlSensitive;
    private MyQuestionnaireDetailsDto myQuestionnaireDetailsDto;
    private Instant instant;

    @BeforeEach
    void setUp() {
        questionnaireApiUrl = "api";
        questionnaireApiUrlSensitive = "apiSensitive";
        questioningAccreditationRepositoryStub = new QuestioningAccreditationRepositoryStub();
        PartitioningServiceStub partitioningService = new PartitioningServiceStub();
        QuestioningEventServiceStub questioningEventServiceStub = new QuestioningEventServiceStub();
        QuestioningAccreditationServiceStub questioningAccreditationService = new QuestioningAccreditationServiceStub();
        Date date = new Date();
        instant = date.toInstant();
        questioningService = new QuestioningServiceStub();

        myQuestionnaireDetailsDto = new MyQuestionnaireDetailsDto();
        questioningAccreditationRepositoryStub.setMyQuestionnaireDetailsDto(List.of(myQuestionnaireDetailsDto));

        myQuestionnaireDetailsDto.setSurveyUnitId("SU123");
        myQuestionnaireDetailsDto.setSurveyUnitIdentificationCode("Code123");
        myQuestionnaireDetailsDto.setSurveyUnitIdentificationName("Name123");
        myQuestionnaireDetailsDto.setSourceId("source1");
        myQuestionnaireDetailsDto.setPartitioningLabel("Partition Label");
        myQuestionnaireDetailsDto.setQuestioningId(1L);
        myQuestionnaireDetailsDto.setPartitioningId("partition1");
        myQuestionnaireDetailsDto.setPartitioningReturnDate(date);

        mySurveysService = new MySurveysServiceImpl(
                questioningAccreditationService,
                partitioningService,
                questioningEventServiceStub,
                questioningService,
                questioningUrlComponent,
                questioningAccreditationRepositoryStub,
                questionnaireApiUrl,
                questionnaireApiUrlSensitive);

        Questioning mockQuestioning = new Questioning();
        mockQuestioning.setId(1L);
        mockQuestioning.setIdPartitioning("partition1");
        questioningService.saveQuestioning(mockQuestioning);

        Partitioning mockPartitioning = new Partitioning();
        mockPartitioning.setLabel("Partition Label");
        mockPartitioning.setId(mockQuestioning.getIdPartitioning());
        partitioningService.setStubbedPartition(mockPartitioning);
    }

    @Test
    @DisplayName("Should return questionnaire list when status is IN_PROGRESS")
    void getListMyQuestionnairesTest() {
        when(questioningUrlComponent.getAccessUrlWithContactId(any(),any(),any(), any())).thenReturn("http://access-url");
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.IN_PROGRESS);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.getPartitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.getSurveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.getSurveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.getQuestioningAccessUrl()).isEqualTo("http://access-url");
        assertThat(dto.getPartitioningId()).isEqualTo("partition1");
        assertThat(dto.getSurveyUnitId()).isEqualTo("SU123");
        assertThat(dto.getQuestioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.IN_PROGRESS.name());
        assertThat(dto.getDepositProofUrl()).isNull();
        assertThat(dto.getPartitioningReturnDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should return questionnaire list when status is INCOMING")
    void getListMyQuestionnairesTest2() {
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.INCOMING);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

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
        assertThat(dto.getPartitioningReturnDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should return questionnaire list when status is RECEIVED with Lunatic")
    void getListMyQuestionnairesTest3() {
        String pathDepositProof = questionnaireApiUrl + "/api/survey-unit/" + "SU123" + "/deposit-proof";

        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.LUNATIC_NORMAL.name());

        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.getPartitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.getSurveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.getSurveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.getPartitioningId()).isEqualTo("partition1");
        assertThat(dto.getSurveyUnitId()).isEqualTo("SU123");
        assertThat(dto.getQuestioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.RECEIVED.name());
        assertThat(dto.getDepositProofUrl()).isEqualTo(pathDepositProof);
        assertThat(dto.getQuestioningAccessUrl()).isNull();
        assertThat(dto.getPartitioningReturnDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should return questionnaire list when status is RECEIVED with XForm")
    void getListMyQuestionnairesTest4() {
        when(questioningUrlComponent.getAccessUrlWithContactId(any(),any(),any(), any())).thenReturn("http://access-url");
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.XFORM1.toString());

        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.getQuestioningAccessUrl()).isEqualTo("http://access-url");
        assertThat(dto.getDepositProofUrl()).isNull();
    }

    @Test
    @DisplayName("Should return questionnaire list when status is RECEIVED with Lunatic Sensitive")
    void getListMyQuestionnairesTest5() {
        String pathDepositProof = questionnaireApiUrlSensitive + "/api/survey-unit/" + "SU123" + "/deposit-proof";
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.LUNATIC_SENSITIVE.toString());

        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.RECEIVED);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.getPartitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.getSurveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.getSurveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.getPartitioningId()).isEqualTo("partition1");
        assertThat(dto.getSurveyUnitId()).isEqualTo("SU123");
        assertThat(dto.getQuestioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.RECEIVED.name());
        assertThat(dto.getDepositProofUrl()).isEqualTo(pathDepositProof);
        assertThat(dto.getQuestioningAccessUrl()).isNull();
        assertThat(dto.getPartitioningReturnDate()).isEqualTo(instant);
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
        when(questioningUrlComponent.getAccessUrlWithContactId(any(),any(),any(), any())).thenReturn("http://access-url");
        questioningService.setQuestionnaireStatus(QuestionnaireStatusTypeEnum.NOT_STARTED);
        List<MyQuestionnaireDto> result = mySurveysService.getListMyQuestionnaires("123");

        assertThat(result).isNotEmpty().hasSize(1);

        MyQuestionnaireDto dto = result.getFirst();
        assertThat(dto.getPartitioningLabel()).isEqualTo("Partition Label");
        assertThat(dto.getSurveyUnitIdentificationCode()).isEqualTo("Code123");
        assertThat(dto.getSurveyUnitIdentificationName()).isEqualTo("Name123");
        assertThat(dto.getQuestioningAccessUrl()).isEqualTo("http://access-url");
        assertThat(dto.getPartitioningId()).isEqualTo("partition1");
        assertThat(dto.getSurveyUnitId()).isEqualTo("SU123");
        assertThat(dto.getQuestioningStatus()).isEqualTo(QuestionnaireStatusTypeEnum.NOT_STARTED.name());
        assertThat(dto.getDepositProofUrl()).isNull();
        assertThat(dto.getPartitioningReturnDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should set access URL for XFORM1 and status RECEIVED")
    void handleStatusTest1() {
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.XFORM1.name());


        MyQuestionnaireDto questionnaireDto = new MyQuestionnaireDto();
        Questioning questioning = new Questioning();
        Partitioning partitioning = new Partitioning();

        questioning.setIdPartitioning("partition1");
        partitioning.setLabel("Label");
        partitioning.setId("partition1");


        when(questioningUrlComponent.getAccessUrlWithContactId(any(), any(), any(), any()))
                .thenReturn("http://mock-url");

        mySurveysService.handleStatus(
                QuestionnaireStatusTypeEnum.RECEIVED,
                myQuestionnaireDetailsDto,
                questionnaireDto,
                questioning,
                partitioning,
                "123"
        );

        assertThat(questionnaireDto.getQuestioningAccessUrl()).isEqualTo("http://mock-url");
        assertThat(questionnaireDto.getDepositProofUrl()).isNull();
    }

    @Test
    @DisplayName("Should set deposit URL for LUNATIC_NORMAL and status RECEIVED")
    void handleStatusTest2() {
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.LUNATIC_NORMAL.name());

        Questioning questioning = new Questioning();
        Partitioning partitioning = new Partitioning();
        MyQuestionnaireDto questionnaireDto = new MyQuestionnaireDto();
        String pathDepositProof = questionnaireApiUrl + "/api/survey-unit/" + myQuestionnaireDetailsDto.getSurveyUnitId() + "/deposit-proof";


        questioning.setIdPartitioning("partition1");
        partitioning.setLabel("Label");
        partitioning.setId("partition1");

        mySurveysService.handleStatus(
                QuestionnaireStatusTypeEnum.RECEIVED,
                myQuestionnaireDetailsDto,
                questionnaireDto,
                questioning,
                partitioning,
                "123"
        );

        assertThat(questionnaireDto.getQuestioningAccessUrl()).isNull();
        assertThat(questionnaireDto.getDepositProofUrl()).isEqualTo(pathDepositProof);
    }

    @Test
    @DisplayName("Should set nothing with a status INCOMING")
    void handleStatusTest3() {

        Questioning questioning = new Questioning();
        Partitioning partitioning = new Partitioning();
        MyQuestionnaireDto questionnaireDto =  spy(new MyQuestionnaireDto());


        questioning.setIdPartitioning("partition1");
        partitioning.setLabel("Label");
        partitioning.setId("partition1");

        mySurveysService.handleStatus(
                QuestionnaireStatusTypeEnum.INCOMING,
                myQuestionnaireDetailsDto,
                questionnaireDto,
                questioning,
                partitioning,
                "123"
        );

        verify(questionnaireDto, never()).setQuestioningAccessUrl(any());
        verify(questionnaireDto, never()).setDepositProofUrl(any());
    }

    @Test
    @DisplayName("Should set access URL for status IN_PROGRESS")
    void handleStatusTest4() {
        myQuestionnaireDetailsDto.setDataCollectionTarget(DataCollectionEnum.LUNATIC_NORMAL.name());

        Questioning questioning = new Questioning();
        Partitioning partitioning = new Partitioning();
        MyQuestionnaireDto questionnaireDto = new MyQuestionnaireDto();

        when(questioningUrlComponent.getAccessUrlWithContactId(any(), any(), any(), any()))
                .thenReturn("http://mock-url");

        questioning.setIdPartitioning("partition1");
        partitioning.setLabel("Label");
        partitioning.setId("partition1");

        mySurveysService.handleStatus(
                QuestionnaireStatusTypeEnum.NOT_STARTED,
                myQuestionnaireDetailsDto,
                questionnaireDto,
                questioning,
                partitioning,
                "123"
        );

        assertThat(questionnaireDto.getQuestioningAccessUrl()).isEqualTo("http://mock-url");
        assertThat(questionnaireDto.getDepositProofUrl()).isNull();
    }

    @Test
    @DisplayName("Should throw exception for unsupported data collection type")
    void handleStatusTest5() {
        myQuestionnaireDetailsDto.setDataCollectionTarget("N/A"); // Invalid enum value

        Questioning questioning = new Questioning();
        Partitioning partitioning = new Partitioning();
        MyQuestionnaireDto questionnaireDto = new MyQuestionnaireDto();

        questioning.setIdPartitioning("partition1");
        partitioning.setLabel("Label");
        partitioning.setId("partition1");

        assertThatThrownBy(() -> mySurveysService.handleStatus(
                QuestionnaireStatusTypeEnum.RECEIVED,
                myQuestionnaireDetailsDto,
                questionnaireDto,
                questioning,
                partitioning,
                "123"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant"); // or whatever message fits
    }

    @Test
    @DisplayName("Should return true for XFORM1 and XFORM2 in isXForm")
    void isXFormShouldReturnTrueForXFormTypes() {
        assertThat(mySurveysService.isXForm(DataCollectionEnum.XFORM1)).isTrue();
        assertThat(mySurveysService.isXForm(DataCollectionEnum.XFORM2)).isTrue();
        assertThat(mySurveysService.isXForm(DataCollectionEnum.LUNATIC_NORMAL)).isFalse();
    }

    @Test
    @DisplayName("Should return true for NOT_STARTED and IN_PROGRESS in isOpen")
    void isOpenShouldReturnTrueForOpenStatuses() {
        assertThat(mySurveysService.isOpen(QuestionnaireStatusTypeEnum.NOT_STARTED)).isTrue();
        assertThat(mySurveysService.isOpen(QuestionnaireStatusTypeEnum.IN_PROGRESS)).isTrue();
        assertThat(mySurveysService.isOpen(QuestionnaireStatusTypeEnum.RECEIVED)).isFalse();
    }

    @Test
    @DisplayName("Should set access URL correctly")
    void setQuestioningAccessUrlShouldSetUrl() {
        MyQuestionnaireDto dto = new MyQuestionnaireDto();
        Questioning questioning = new Questioning();
        Partitioning partitioning = new Partitioning();

        when(questioningUrlComponent.getAccessUrlWithContactId(any(), any(), any(), any()))
                .thenReturn("http://url");

        mySurveysService.setQuestioningAccessUrl(dto, questioning, partitioning, "999");

        assertThat(dto.getQuestioningAccessUrl()).isEqualTo("http://url");
    }

    @Test
    @DisplayName("Should build correct deposit proof URL based on collection type")
    void buildDepositProofUrlShouldReturnCorrectUrls() {
        MyQuestionnaireDetailsDto dto = new MyQuestionnaireDetailsDto();
        dto.setSurveyUnitId("SU42");

        String path = "/api/survey-unit/SU42/deposit-proof";

        assertThat(mySurveysService.buildDepositProofUrl(dto, DataCollectionEnum.LUNATIC_NORMAL))
                .isEqualTo(questionnaireApiUrl + path);

        assertThat(mySurveysService.buildDepositProofUrl(dto, DataCollectionEnum.LUNATIC_SENSITIVE))
                .isEqualTo(questionnaireApiUrlSensitive + path);

        assertThat(mySurveysService.buildDepositProofUrl(dto, DataCollectionEnum.XFORM1))
                .isNull();
    }

}
