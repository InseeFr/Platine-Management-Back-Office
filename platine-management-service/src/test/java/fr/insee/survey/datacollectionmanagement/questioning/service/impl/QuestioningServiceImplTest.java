package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.InterrogationEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.InterrogationEventOrderRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private QuestioningEventService questioningEventService;

    @Mock
    private QuestioningAccreditationService questioningAccreditationService;

    private final ModelMapper modelMapper = new ModelMapper();

    @Mock
    private PartitioningRepository partitioningRepository;

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

        questioningService = new QuestioningServiceImpl(
                interrogationEventComparator, questioningRepository, questioningUrlComponent, surveyUnitService,
                partitioningService, contactService, questioningEventService, questioningAccreditationService,
                modelMapper, partitioningRepository);
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
        questioning = new Questioning();
        questioning.setId(1L);
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
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.INCOMING);
    }
    @Test
    @DisplayName("Should return QuestioningDetailsDto when questioning exists")
    void testGetQuestioningDetails() {
        // Given
        Long questioningId = 1L;
        questioning = new Questioning();
        questioning.setId(questioningId);
        SurveyUnit su = new SurveyUnit();
        su.setIdSu("1");
        su.setIdentificationName("identificationName");
        su.setIdentificationCode("identificationCode");
        su.setLabel("label");
        questioning.setSurveyUnit(su);
        partitioning = new Partitioning();
        partitioning.setId("1");
        Campaign campaign = new Campaign();
        campaign.setId("CAMP123");
        Survey survey = new Survey();
        survey.setId("SURVEY123");
        Source source = new Source();
        source.setId("SOURCEID");
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

        when(contactService.findByIdentifiers(any())).thenReturn(List.of(new QuestioningContactDto("contact1", "Doe", "John", true)));
        when(questioningEventService.getLastQuestioningEvent(any(), any())).thenReturn(Optional.empty());

        when(questioningEventService.getLastQuestioningEvent(any(), any())).thenReturn(Optional.empty());

        // when
        QuestioningDetailsDto result = questioningService.getQuestioningDetails(questioningId);

        // then
        assertThat(result).isNotNull();
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
        Long questioningId = 99L;
        when(questioningRepository.findById(questioningId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> questioningService.getQuestioningDetails(questioningId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Questioning 99 not found");
    }

    @DisplayName("Should return NOT_RECEIVED when no events exist")
    @Test
    void getQuestioningStatusTest2() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        questioning.setQuestioningEvents(new HashSet<>());
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when refused event exists")
    @Test
    void getQuestioningStatusTest3() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.REFUSAL);
        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(true);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return RECEIVED when validated event exists before closing date")
    @Test
    void getQuestioningStatusTest4() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.VALINT);
        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(false);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.OPENED_EVENTS)).thenReturn(true);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.VALIDATED_EVENTS)).thenReturn(true);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.RECEIVED);
    }

    @DisplayName("Should return NOT_STARTED when interrogation not opened by user but accessible before closing date")
    @Test
    void getQuestioningStatusTest5() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.INITLA);
        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.VALIDATED_EVENTS)).thenReturn(false);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(false);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.OPENED_EVENTS)).thenReturn(true);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_STARTED);
    }

    @DisplayName("Should return NOT_RECEIVED when no valid event exists after closing date")
    @Test
    void getQuestioningStatusTest6() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 96400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        Set<QuestioningEvent> events = new HashSet<>();
        questioning.setQuestioningEvents(events);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when valid events exist after closing date")
    @Test
    void getQuestioningStatusTest7() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 96400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.VALINT);
        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(false);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when valid and refused events exist before closing date")
    @Test
    void getQuestioningStatusTest8() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 96400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEventValid = new QuestioningEvent();
        questioningEventValid.setType(TypeQuestioningEvent.VALINT);
        QuestioningEvent questioningEventRefused = new QuestioningEvent();
        questioningEventRefused.setType(TypeQuestioningEvent.HC);
        events.add(questioningEventValid);
        events.add(questioningEventRefused);

        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(false);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return IN_PROGRESS when user started interrogation before closing date")
    @Test
    void getQuestioningStatusTest9() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEventOpen = new QuestioningEvent();
        questioningEventOpen.setType(TypeQuestioningEvent.INITLA);
        QuestioningEvent questioningEventStarted = new QuestioningEvent();
        questioningEventStarted.setType(TypeQuestioningEvent.PARTIELINT);
        events.add(questioningEventOpen);
        events.add(questioningEventStarted);

        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.VALIDATED_EVENTS)).thenReturn(false);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(false);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.OPENED_EVENTS)).thenReturn(true);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.STARTED_EVENTS)).thenReturn(true);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.IN_PROGRESS);
    }

    @Test
    @DisplayName("searchQuestioning with param should query repository findQuestioningByParam and map results")
    void testSearchQuestioningWithParam() {
        // Given
        String param = "abc";
        Questioning q1 = buildQuestioning(1L, "SU1");
        Questioning q2 = buildQuestioning(2L, "SU2");
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
        assertThat(page.getContent()).extracting("questioningId").containsExactlyInAnyOrder(1L, 2L);
        verify(questioningRepository).findQuestioningByParam("ABC");
        verify(questioningRepository, never()).findQuestioningIds(any());
        verify(questioningRepository, never()).findQuestioningsByIds(any());
    }

    @Test
    @DisplayName("searchQuestioning without param should retrieve ids page then full questionings")
    void testSearchQuestioningWithoutParam() {
        // Given
        String param = "";
        Questioning q1 = buildQuestioning(1L, "SU1");
        Questioning q2 = buildQuestioning(2L, "SU2");
        Campaign c = new Campaign();
        c.setId("CAMP01");
        partitioning.setCampaign(c);
        when(partitioningService.findById(any())).thenReturn(partitioning);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Long> idsPage = new PageImpl<>(List.of(1L, 2L), pageable, 2);
        when(questioningRepository.findQuestioningIds(pageable)).thenReturn(idsPage);
        when(questioningRepository.findQuestioningsByIds(List.of(1L, 2L))).thenReturn(List.of(q1, q2));

        // When
        Page<SearchQuestioningDto> page = questioningService.searchQuestioning(param, pageable);

        // Then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting("questioningId").containsExactlyInAnyOrder(1L, 2L);
        verify(questioningRepository).findQuestioningIds(pageable);

        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(questioningRepository).findQuestioningsByIds(captor.capture());
        assertThat(captor.getValue()).containsExactlyInAnyOrder(1L, 2L);

        verify(questioningRepository, never()).findQuestioningByParam(any());
    }

    private Questioning buildQuestioning(Long id, String suId) {
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