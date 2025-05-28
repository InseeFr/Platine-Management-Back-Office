package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SourceTypeEnum;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.ParametersService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.AssistanceDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.InterrogationEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.dao.search.SearchQuestioningDao;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.InterrogationEventOrderRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningEventServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestioningServiceImplTest {


    private static final String SURVEY_UNIT_ID = "12345";

    @Mock
    private InterrogationEventOrderRepository eventOrderRepository;

    @Mock
    private QuestioningRepository questioningRepository;

    @Mock
    private SurveyUnitService surveyUnitService;

    @Mock
    private PartitioningService partitioningService;

    @Mock
    private ContactService contactService;

    private QuestioningEventServiceStub questioningEventService;

    @Mock
    private SearchQuestioningDao searchQuestioningDao;

    @Mock
    private QuestioningAccreditationService questioningAccreditationService;

    @Mock
    private SourceRepository sourceRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Mock
    private PartitioningRepository partitioningRepository;

    @Mock
    private ParametersService parametersService;

    private Questioning questioning = initQuestioning();

    private QuestioningServiceImpl questioningService;

    @Mock
    private QuestioningUrlComponent questioningUrlComponent;

    private Partitioning partitioning;

    private static final int O_INITLA     = 1;
    private static final int O_PARTIEL_VAL = 2;
    private static final int O_REF_WAST   = 3;
    private static final int O_HC         = 4;

    @BeforeEach
    void setUp() {

        partitioning = new Partitioning();

        when(eventOrderRepository.findAll()).thenReturn(List.of(
                order("INITLA",     O_INITLA),
                order("PARTIELINT", O_PARTIEL_VAL),
                order("VALINT",     O_PARTIEL_VAL),
                order("VALPAP",     O_PARTIEL_VAL),
                order("REFUSAL",    O_REF_WAST),
                order("WASTE",      O_REF_WAST),
                order("HC",         O_HC)
        ));

        InterrogationEventComparator interrogationEventComparator = new InterrogationEventComparator(eventOrderRepository);

        questioningEventService = new QuestioningEventServiceStub();

        questioningService = new QuestioningServiceImpl(
                interrogationEventComparator, questioningRepository, searchQuestioningDao, questioningUrlComponent, surveyUnitService,
                partitioningService, contactService, questioningEventService, questioningAccreditationService,
                modelMapper, partitioningRepository, parametersService, sourceRepository);
    }
    private static InterrogationEventOrder order(String status, int valeur) {
        return new InterrogationEventOrder(null, status, valeur);
    }


    @Test
    @DisplayName("Check notFoundException when 0 questioning found for 1 surveyUnit and one camapaign")
    void findByCampaignIdAndSurveyUnitIdSuEmptyResult() {
        String campaignId = "CAMP2025X00";
        String surveyUnitId = "SURVEYUNITID";
        List<Questioning> listQuestioning = new ArrayList<>();

        when(questioningRepository.findQuestioningByCampaignIdAndSurveyUnitId(campaignId, surveyUnitId)).thenReturn(listQuestioning);

        assertThatThrownBy(() -> questioningService.findByCampaignIdAndSurveyUnitIdSu(campaignId, surveyUnitId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No questioning found for campaignId");
    }

    @Test
    @DisplayName("Check TooManyValuesException when 2 questioning found for 1 surveyUnit and onr campaign")
    void findByCampaignIdAndSurveyUnitIdSuTwoResults() {
        String campaignId = "CAMP2025X00";
        String surveyUnitId = "SURVEYUNITID";
        List<Questioning> listQuestioning = new ArrayList<>();
        listQuestioning.add(initQuestioning());
        listQuestioning.add(initQuestioning());

        when(questioningRepository.findQuestioningByCampaignIdAndSurveyUnitId(campaignId, surveyUnitId)).thenReturn(listQuestioning);

        assertThatThrownBy(() -> questioningService.findByCampaignIdAndSurveyUnitIdSu(campaignId, surveyUnitId))
                .isInstanceOf(TooManyValuesException.class)
                .hasMessageContaining("2 questionings found for");
    }

    @Test
    @DisplayName("Check ok when 1 and only 1 questioning found for 1 surveyUnit and onr campaign")
    void findByCampaignIdAndSurveyUnitIdSuOneResult() {
        String campaignId = "CAMP2025X00";
        String surveyUnitId = "SURVEYUNITID";
        List<Questioning> listQuestioning = new ArrayList<>();
        Questioning q = initQuestioning();
        listQuestioning.add(q);

        when(questioningRepository.findQuestioningByCampaignIdAndSurveyUnitId(campaignId, surveyUnitId)).thenReturn(listQuestioning);

        assertThat(questioningService.findByCampaignIdAndSurveyUnitIdSu(campaignId, surveyUnitId).getQuestioningId()).isEqualTo(q.getId());
    }

    private Questioning initQuestioning() {
        UUID questioningId = UUID.randomUUID();
        questioning = new Questioning();
        questioning.setId(questioningId);
        SurveyUnit su = new SurveyUnit();
        su.setIdSu(SURVEY_UNIT_ID);
        questioning.setSurveyUnit(su);
        questioning.setModelName("MODEL");

        return questioning;
    }

    @DisplayName("Should return INCOMING when today is before opening date")
    @Test
    void getQuestioningStatusTest() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.INCOMING);
    }

    @ParameterizedTest
    @CsvSource({
        "HOUSEHOLD, true",
        "BUSINESS, false"
    })
    @DisplayName("Should return correct QuestioningDetailsDto based on SourceTypeEnum")
    void testGetQuestioningDetails(SourceTypeEnum sourceType, boolean expectedIsHousehold) {
        // Given
        UUID questioningId = UUID.randomUUID();
        questioning = new Questioning();
        questioning.setId(questioningId);
        SurveyUnit su = new SurveyUnit();
        su.setIdSu("1");
        su.setIdentificationName("identificationName");
        su.setIdentificationCode("identificationCode");
        su.setLabel("label");
        questioning.setSurveyUnit(su);

        partitioning.setId("1");

        Campaign campaign = new Campaign();
        campaign.setId("CAMP123");

        Survey survey = new Survey();
        survey.setId("SURVEY123");

        Source source = new Source();
        source.setId("CAMP123");
        source.setType(sourceType);

        survey.setSource(source);
        campaign.setSurvey(survey);
        partitioning.setCampaign(campaign);
        QuestioningAccreditation questioningAccreditation = new QuestioningAccreditation();
        questioningAccreditation.setIdContact("contactId");
        questioning.setQuestioningAccreditations(Set.of(questioningAccreditation));

        QuestioningEvent event = new QuestioningEvent(
            new Date(),
            TypeQuestioningEvent.INITLA,
            questioning);
        questioning.setQuestioningEvents(Set.of(event));
        questioning.setQuestioningComments(Set.of());
        questioning.setQuestioningCommunications(Set.of());

        when(questioningRepository.findById(questioningId)).thenReturn(Optional.of(questioning));
        when(partitioningRepository.findById(any())).thenReturn(Optional.of(partitioning));
        when(sourceRepository.findById(any())).thenReturn(Optional.of(source));
        when(contactService.findByIdentifiers(any())).thenReturn(
            List.of(new QuestioningContactDto("contact1", "Doe", "John", true))
        );

        // When
        QuestioningDetailsDto result = questioningService.getQuestioningDetails(questioningId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIsHousehold()).isEqualTo(expectedIsHousehold);
        assertThat(result.getQuestioningId()).isEqualTo(questioningId);
        assertThat(result.getCampaignId()).isEqualTo("CAMP123");
        assertThat(result.getSurveyUnitId()).isEqualTo("1");
        assertThat(result.getSurveyUnitIdentificationCode()).isEqualTo("identificationCode");
        assertThat(result.getSurveyUnitIdentificationName()).isEqualTo("identificationName");
        assertThat(result.getSurveyUnitLabel()).isEqualTo("label");
        assertThat(result.getListContacts()).isNotEmpty();
        assertThat(result.getListContacts().getFirst().identifier()).isEqualTo("contact1");
    }

    @Test
    @DisplayName("Should throw NotFoundException when questioning does not exist")
    void shouldThrowNotFoundExceptionWhenQuestioningNotFound() {
        // Given
        UUID questioningId = UUID.randomUUID();
        when(questioningRepository.findById(questioningId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> questioningService.getQuestioningDetails(questioningId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Questioning "+questioningId+" not found");
    }

    @DisplayName("Should return NOT_RECEIVED when no events exist")
    @Test
    void getQuestioningStatusTest2() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when refused event exists")
    @Test
    void getQuestioningStatusTest3() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        List<QuestioningEventDto> events = new ArrayList<>();
        QuestioningEventDto questioningEvent = new QuestioningEventDto();
        questioningEvent.setType(TypeQuestioningEvent.REFUSAL.name());
        events.add(questioningEvent);
        questioningEventService.setQuestioningEvents(events);

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return RECEIVED when validated event exists before closing date")
    @Test
    void getQuestioningStatusTest4() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        List<QuestioningEventDto> events = new ArrayList<>();
        QuestioningEventDto questioningEvent = new QuestioningEventDto();
        questioningEvent.setType(TypeQuestioningEvent.VALINT.name());
        events.add(questioningEvent);
        questioningEventService.setQuestioningEvents(events);

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.RECEIVED);
    }

    @DisplayName("Should return NOT_STARTED when interrogation not opened by user but accessible before closing date")
    @Test
    void getQuestioningStatusTest5() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        List<QuestioningEventDto> events = new ArrayList<>();
        QuestioningEventDto questioningEvent = new QuestioningEventDto();
        questioningEvent.setType(TypeQuestioningEvent.INITLA.name());
        events.add(questioningEvent);
        questioningEventService.setQuestioningEvents(events);

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_STARTED);
    }

    @DisplayName("Should return NOT_RECEIVED when no valid event exists after closing date")
    @Test
    void getQuestioningStatusTest6() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 96400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when valid events exist after closing date")
    @Test
    void getQuestioningStatusTest7() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 96400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        List<QuestioningEventDto> events = new ArrayList<>();
        QuestioningEventDto questioningEvent = new QuestioningEventDto();
        questioningEvent.setType(TypeQuestioningEvent.VALINT.name());
        events.add(questioningEvent);
        questioningEventService.setQuestioningEvents(events);

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when valid and refused events exist before closing date")
    @Test
    void getQuestioningStatusTest8() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 96400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        List<QuestioningEventDto> events = new ArrayList<>();
        QuestioningEventDto questioningEvent = new QuestioningEventDto();
        questioningEvent.setType(TypeQuestioningEvent.VALINT.name());
        events.add(questioningEvent);
        QuestioningEventDto questioningEvent2 = new QuestioningEventDto();
        questioningEvent2.setType(TypeQuestioningEvent.HC.name());
        events.add(questioningEvent2);
        questioningEventService.setQuestioningEvents(events);

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return IN_PROGRESS when user started interrogation before closing date")
    @Test
    void getQuestioningStatusTest9() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        List<QuestioningEventDto> events = new ArrayList<>();
        QuestioningEventDto questioningEvent = new QuestioningEventDto();
        questioningEvent.setType(TypeQuestioningEvent.INITLA.name());
        events.add(questioningEvent);
        QuestioningEventDto questioningEvent2 = new QuestioningEventDto();
        questioningEvent2.setType(TypeQuestioningEvent.PARTIELINT.name());
        events.add(questioningEvent2);
        questioningEventService.setQuestioningEvents(events);

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.IN_PROGRESS);
    }

    @Test
    @DisplayName("searchQuestioning with param should query repository findQuestioningByParam and map results")
    void testSearchQuestioningWithParam() {
        // Given
        UUID questioningId1 = UUID.randomUUID();
        UUID questioningId2 = UUID.randomUUID();
        String param = "abc";
        Questioning q1 = buildQuestioning(questioningId1, "SU1");
        Questioning q2 = buildQuestioning(questioningId2, "SU2");
        Campaign c = new Campaign();
        c.setId("CAMP01");
        partitioning.setCampaign(c);
        when(partitioningService.findById(any())).thenReturn(partitioning);
        when(questioningRepository.findQuestioningByParam("ABC")).thenReturn(List.of(q1, q2));
        Pageable pageable = PageRequest.of(0, 10);


        // When
        Page<SearchQuestioningDto> page = questioningService.searchQuestioning(param, pageable);

        // Then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting("questioningId").containsExactlyInAnyOrder(questioningId1, questioningId2);
        verify(questioningRepository).findQuestioningByParam("ABC");
        verify(questioningRepository, never()).findQuestioningIds(any());
        verify(questioningRepository, never()).findQuestioningsByIds(any());
    }

    @Test
    @DisplayName("searchQuestioning without param should retrieve ids page then full questionings")
    void testSearchQuestioningWithoutParam() {
        // Given
        UUID questioningId1 = UUID.randomUUID();
        UUID questioningId2 = UUID.randomUUID();
        String param = "";
        Questioning q1 = buildQuestioning(questioningId1, "SU1");
        Questioning q2 = buildQuestioning(questioningId2, "SU2");
        Campaign c = new Campaign();
        c.setId("CAMP01");
        partitioning.setCampaign(c);
        when(partitioningService.findById(any())).thenReturn(partitioning);
        Pageable pageable = PageRequest.of(0, 10);
        Page<UUID> idsPage = new PageImpl<>(List.of(questioningId1,questioningId2), pageable, 2);
        when(questioningRepository.findQuestioningIds(pageable)).thenReturn(idsPage);
        when(questioningRepository.findQuestioningsByIds(List.of(questioningId1,questioningId2))).thenReturn(List.of(q1, q2));

        // When
        Page<SearchQuestioningDto> page = questioningService.searchQuestioning(param, pageable);

        // Then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting("questioningId").containsExactlyInAnyOrder(questioningId1, questioningId2);
        verify(questioningRepository).findQuestioningIds(pageable);

        ArgumentCaptor<List<UUID>> captor = ArgumentCaptor.forClass(List.class);
        verify(questioningRepository).findQuestioningsByIds(captor.capture());
        assertThat(captor.getValue()).containsExactlyInAnyOrder(questioningId1,questioningId2);

        verify(questioningRepository, never()).findQuestioningByParam(any());
    }

    @Test
    @DisplayName("getMailAssistance without personalisation returns null")
    void getMailAssistanceDtoNoMail() {
        UUID questioningId1 = UUID.randomUUID();
        Questioning q1 = buildQuestioning(questioningId1, "SU1");

        when(questioningRepository.findById(questioningId1)).thenReturn(Optional.of(q1));

        AssistanceDto assistanceDto = questioningService.getMailAssistanceDto(questioningId1);
        assertNull(assistanceDto.getMailAssistance());
        assertThat(assistanceDto.getSurveyUnitId()).isEqualTo("SU1");
    }

    @Test
    @DisplayName("getMailAssistance with questioning personalisation returns the right mail")
    void getMailAssistanceDtoQuestioningMail() {
        UUID questioningId1 = UUID.randomUUID();
        Questioning q1 = buildQuestioning(questioningId1, "SU1");
        String assistanceMail = "assistanceq1@assistance.fr";

        q1.setAssistanceMail(assistanceMail);
        when(questioningRepository.findById(questioningId1)).thenReturn(Optional.of(q1));

        AssistanceDto assistanceDto = questioningService.getMailAssistanceDto(questioningId1);
        assertThat(assistanceDto.getMailAssistance()).isEqualTo(assistanceMail);
        assertThat(assistanceDto.getSurveyUnitId()).isEqualTo("SU1");

    }

    @Test
    @DisplayName("getMailAssistance with campaign personalisation returns the right mail")
    void getMailAssistanceDtoCampaignMail() {
        UUID questioningId1 = UUID.randomUUID();
        Questioning q1 = buildQuestioning(questioningId1, "SU1");
        String assistancePart = "assistancePart@assistance.fr";

        when(questioningRepository.findById(questioningId1)).thenReturn(Optional.of(q1));
        when(partitioningService.findById(q1.getIdPartitioning())).thenReturn(partitioning);
        when(parametersService.findSuitableParameterValue(partitioning, ParameterEnum.MAIL_ASSISTANCE)).thenReturn(assistancePart);

        AssistanceDto assistanceDto = questioningService.getMailAssistanceDto(questioningId1);
        assertThat(assistanceDto.getMailAssistance()).isEqualTo(assistancePart);
        assertThat(assistanceDto.getSurveyUnitId()).isEqualTo("SU1");

    }


    private Questioning buildQuestioning(UUID id, String suId) {
        Questioning q = new Questioning();
        q.setId(id);
        q.setIdPartitioning("PART001");

        SurveyUnit su = new SurveyUnit();
        su.setIdSu(suId);
        su.setIdentificationCode("IC_" + suId);
        q.setSurveyUnit(su);
        QuestioningEvent event = new QuestioningEvent();
        event.setType(TypeQuestioningEvent.INITLA);
        event.setQuestioning(q);
        event.setId(1L);
        event.setDate(new Date(System.currentTimeMillis()));
        QuestioningEvent event2 = new QuestioningEvent();
        event2.setType(TypeQuestioningEvent.VALINT);
        event2.setQuestioning(q);
        event2.setId(2L);
        event2.setDate(new Date(System.currentTimeMillis()));
        q.setQuestioningAccreditations(new HashSet<>());
        q.setQuestioningEvents(Set.of(event, event2));
        q.setQuestioningCommunications(new HashSet<>());
        return q;
    }


}