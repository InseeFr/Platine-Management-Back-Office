package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.QuestioningCsvDto;
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
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningProbationDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.InterrogationEventOrderRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningEventServiceStub;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class QuestioningServiceImplTest {


    private static final String SURVEY_UNIT_ID = "12345";

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

    @BeforeEach
    void setUp() {

        partitioning = new Partitioning();

        InterrogationEventComparator interrogationEventComparator = new InterrogationEventComparator(new InterrogationEventOrderRepositoryStub());

        questioningEventService = new QuestioningEventServiceStub();

        questioningService = new QuestioningServiceImpl(
                interrogationEventComparator, questioningRepository, searchQuestioningDao, questioningUrlComponent,
                contactService, questioningEventService,
                modelMapper, partitioningRepository, parametersService, sourceRepository);
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
        questioning.setIsOnProbation(true);

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
        assertThat(result.getIsOnProbation()).isTrue();
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
        partitioning.setOpeningDate(addDays(new Date(), -1)); // Yesterday

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when refused event exists")
    @Test
    void getQuestioningStatusTest3() {
        partitioning.setOpeningDate(addDays(new Date(), -1)); // Yesterday
        partitioning.setClosingDate(addDays(new Date(), +1)); // Tomorrow
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
        partitioning.setOpeningDate(addDays(new Date(), -1)); // Yesterday
        partitioning.setClosingDate(addDays(new Date(), +1)); // Tomorrow
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
        partitioning.setOpeningDate(addDays(new Date(), -1)); // Yesterday
        partitioning.setClosingDate(addDays(new Date(), +1)); // Tomorrow
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
        partitioning.setOpeningDate(addDays(new Date(), -1)); // Yesterday
        partitioning.setClosingDate(addDays(new Date(), -1)); // Yesterday

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning.getId(), partitioning.getOpeningDate(), partitioning.getClosingDate());
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when valid events exist after closing date")
    @Test
    void getQuestioningStatusTest7() {
        partitioning.setOpeningDate(addDays(new Date(), -1)); // Yesterday
        partitioning.setClosingDate(addDays(new Date(), -1)); // Yesterday
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
        partitioning.setOpeningDate(addDays(new Date(), -1)); // Yesterday
        partitioning.setClosingDate(addDays(new Date(), +1)); // Tomorrow
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
        partitioning.setOpeningDate(addDays(new Date(), -1)); // Yesterday
        partitioning.setClosingDate(addDays(new Date(), +1)); // Tomorrow
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
    @DisplayName("getMailAssistance without personalisation returns null")
    void getMailAssistanceDtoNoMail() {
        UUID questioningId1 = UUID.randomUUID();
        Questioning q1 = buildQuestioning(questioningId1, "SU1");

        when(questioningRepository.findById(questioningId1)).thenReturn(Optional.of(q1));
        when(partitioningRepository.findById(q1.getIdPartitioning())).thenReturn(Optional.ofNullable(partitioning));

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
        when(partitioningRepository.findById(q1.getIdPartitioning())).thenReturn(Optional.ofNullable(partitioning));
        when(parametersService.findSuitableParameterValue(partitioning, ParameterEnum.MAIL_ASSISTANCE)).thenReturn(assistancePart);

        AssistanceDto assistanceDto = questioningService.getMailAssistanceDto(questioningId1);
        assertThat(assistanceDto.getMailAssistance()).isEqualTo(assistancePart);
        assertThat(assistanceDto.getSurveyUnitId()).isEqualTo("SU1");

    }

    @Test
    @DisplayName("getQuestioningStatusFileUpload returns INCOMING when today is before openingDate")
    void shouldReturnIncomingWhenBeforeOpeningDate() {
        Date openingDate = addDays(new Date(), 2);
        Date closingDate = addDays(new Date(), 10);

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatusFileUpload(openingDate, closingDate);

        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.INCOMING);
    }

    @Test
    @DisplayName("getQuestioningStatusFileUpload returns RECEIVED when today is after closingDate")
    void shouldReturnReceivedWhenAfterClosingDate() {
        Date openingDate = addDays(new Date(), -10);
        Date closingDate = addDays(new Date(), -2);

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatusFileUpload(openingDate, closingDate);

        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.RECEIVED);
    }

    @Test
    @DisplayName("getQuestioningStatusFileUpload returns IN_PROGRESS when today is between openingDate and closingDate")
    void shouldReturnInProgressWhenBetweenDates() {
        Date openingDate = addDays(new Date(), -2);
        Date closingDate = addDays(new Date(), 2);

        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatusFileUpload(openingDate, closingDate);

        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.IN_PROGRESS);
    }

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
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

    @ParameterizedTest
    @MethodSource("hasExpertiseStatusScenarios")
    void testHasExpertiseStatus(TypeQuestioningEvent event, boolean expected) {
        UUID id = UUID.randomUUID();
        Questioning q = new Questioning();
        q.setHighestEventType(event);

        when(questioningRepository.findById(id)).thenReturn(Optional.of(q));

        assertThat(questioningService.hasExpertiseStatus(id)).isEqualTo(expected);
    }

    private static Stream<Arguments> hasExpertiseStatusScenarios() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of(TypeQuestioningEvent.REFUSAL, false),
                Arguments.of(TypeQuestioningEvent.EXPERT, true)
        );
    }

    @Test
    void getQuestioningsByCampaignIdForCsv_shouldReturnListWhenDataExists() {
      // Given
      String campaignId = "campaign123";
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();

      List<QuestioningCsvDto> expectedDtos = Arrays.asList(
          new QuestioningCsvDto(
              id1,
              "p1",
              "su1",
              TypeQuestioningEvent.VALINT,
              new Date()
          ),
          new QuestioningCsvDto(
              id2,
              "p2",
              "su2",
              TypeQuestioningEvent.FOLLOWUP,
              new Date(System.currentTimeMillis() - 86400000)  // Date d'hier
          )
      );

      when(questioningRepository.findQuestioningDataForCsvByCampaignId(campaignId))
          .thenReturn(expectedDtos);

      // When
      List<QuestioningCsvDto> result = questioningService.getQuestioningsByCampaignIdForCsv(campaignId);

      // Then
      assertNotNull(result);
      assertEquals(2, result.size());
      assertEquals(expectedDtos, result);
      assertEquals(id1, result.getFirst().getInterrogationId());
      assertEquals(TypeQuestioningEvent.VALINT, result.getFirst().getHighestEventType());
    }

    @Test
    void getQuestioningsByCampaignIdForCsv_shouldReturnEmptyListWhenNoData() {
      // Given
      String campaignId = "campaign123";

      when(questioningRepository.findQuestioningDataForCsvByCampaignId(campaignId))
          .thenReturn(Collections.emptyList());

      // When
      List<QuestioningCsvDto> result = questioningService.getQuestioningsByCampaignIdForCsv(campaignId);

      // Then
      assertNotNull(result);
      assertTrue(result.isEmpty());
    }

    @Test
    void getQuestioningsByCampaignIdForCsv_shouldHandleNullCampaignId() {
      // Given
      when(questioningRepository.findQuestioningDataForCsvByCampaignId(null))
          .thenReturn(Collections.emptyList());

      // When/Then
      assertDoesNotThrow(() -> {
        List<QuestioningCsvDto> result = questioningService.getQuestioningsByCampaignIdForCsv(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
      });
    }

    @Test
    void getQuestioningsByCampaignIdForCsv_shouldReturnSingleItemWhenOnlyOneExists() {
      // Given
      String campaignId = "campaign123";
      UUID id = UUID.randomUUID();
      QuestioningCsvDto expectedDto = new QuestioningCsvDto(
          id,
          "p1",
          "su1",
          TypeQuestioningEvent.PND,
          new Date()
      );

      when(questioningRepository.findQuestioningDataForCsvByCampaignId(campaignId))
          .thenReturn(Collections.singletonList(expectedDto));

      // When
      List<QuestioningCsvDto> result = questioningService.getQuestioningsByCampaignIdForCsv(campaignId);

      // Then
      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals(expectedDto, result.getFirst());
      assertEquals(id, result.getFirst().getInterrogationId());
      assertEquals(TypeQuestioningEvent.PND, result.getFirst().getHighestEventType());
    }

    @Test
    void updateQuestioningProbation_shouldUpdateAndReturnDto() {
        // Given
        UUID id = UUID.randomUUID();
        QuestioningProbationDto dto = new QuestioningProbationDto(id, true);

        Questioning existingQuestioning = new Questioning();
        existingQuestioning.setId(id);
        existingQuestioning.setIsOnProbation(false);

        when(questioningRepository.findById(id)).thenReturn(Optional.of(existingQuestioning));
        when(questioningRepository.save(existingQuestioning)).thenReturn(existingQuestioning);

        // When
        QuestioningProbationDto result = questioningService.updateQuestioningProbation(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.questioningId()).isEqualTo(id);
        assertThat(result.isOnProbation()).isTrue();
        assertThat(existingQuestioning.getIsOnProbation()).isTrue();
        verify(questioningRepository).save(existingQuestioning);
    }

    @Test
    void updateQuestioningProbation_shouldThrowNotFoundException_whenQuestioningDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        QuestioningProbationDto dto = new QuestioningProbationDto(id, true);

        when(questioningRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> questioningService.updateQuestioningProbation(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Questioning not found with id " + id);
    }

}