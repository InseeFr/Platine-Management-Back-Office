package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningCommunicationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class QuestioningCommunicationServiceImplTest {

  @Mock
  private QuestioningRepository questioningRepository;

  @Mock
  private QuestioningCommunicationRepository questioningCommunicationRepository;

  private ModelMapper modelMapper = new ModelMapper();

  private QuestioningCommunicationServiceImpl questioningCommunicationService;

  @BeforeEach
  void setUp() {
    questioningCommunicationService =
        new QuestioningCommunicationServiceImpl(modelMapper, questioningRepository, questioningCommunicationRepository);
  }

  @Test
  void shouldReturnDtoListWhenQuestioningExistsWithCommunications() {
    UUID questioningId = UUID.randomUUID();
    QuestioningCommunication communication1 = new QuestioningCommunication();
    QuestioningCommunication communication2 = new QuestioningCommunication();
    List<QuestioningCommunication> communications = List.of(communication1, communication2);
    when(questioningCommunicationRepository.findByQuestioningId(questioningId)).thenReturn(communications);

    List<QuestioningCommunicationDto> result =
        questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(questioningId);

    assertThat(result).isNotNull();
    assertEquals(2, result.size());
    assertThat(result.getFirst()).isNotNull();
    assertThat(result.get(1)).isNotNull();
  }

  @Test
  void shouldReturnEmptyListWhenNoCommunicationsFound() {
    UUID questioningId = UUID.randomUUID();
    when(questioningCommunicationRepository.findByQuestioningId(questioningId)).thenReturn(List.of());

    List<QuestioningCommunicationDto> result =
        questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(questioningId);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldCreateQuestioningCommunicationWhenQuestioningExists() {
    UUID questioningId = UUID.randomUUID();
    Questioning questioning = new Questioning();
    questioning.setQuestioningCommunications(new HashSet<>());

    QuestioningCommunicationInputDto inputDto = new QuestioningCommunicationInputDto(questioningId, Date.from(Instant.now()), StatusCommunication.AUTOMATIC);

    when(questioningRepository.findById(questioningId)).thenReturn(Optional.of(questioning));
    when(questioningCommunicationRepository.save(any(QuestioningCommunication.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    boolean result = questioningCommunicationService.postQuestioningCommunication("COURRIER_CNR", inputDto);

    assertTrue(result);
    verify(questioningCommunicationRepository, times(1)).save(any(QuestioningCommunication.class));
    assertThat(questioning.getQuestioningCommunications()).hasSize(1);

    QuestioningCommunication saved = questioning.getQuestioningCommunications().iterator().next();
    assertEquals(TypeCommunicationEvent.COURRIER_CNR, saved.getType());
    assertEquals(StatusCommunication.AUTOMATIC, saved.getStatus());
    assertEquals(questioning, saved.getQuestioning());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenQuestioningDoesNotExist() {
    UUID questioningId = UUID.randomUUID();
    QuestioningCommunicationInputDto inputDto = new QuestioningCommunicationInputDto(questioningId, Date.from(Instant.now()), StatusCommunication.AUTOMATIC);
    when(questioningRepository.findById(questioningId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () ->
        questioningCommunicationService.postQuestioningCommunication("EMAIL", inputDto));
    verify(questioningCommunicationRepository, never()).save(any());
  }

  @Test
  void shouldSetCorrectFieldsOnSavedCommunication() {
    UUID questioningId = UUID.randomUUID();
    Questioning questioning = new Questioning();
    questioning.setQuestioningCommunications(new HashSet<>());

    Date date = Date.from(Instant.now());
    QuestioningCommunicationInputDto inputDto = new QuestioningCommunicationInputDto(questioningId, date,StatusCommunication.MANUAL );

    when(questioningRepository.findById(questioningId)).thenReturn(Optional.of(questioning));
    when(questioningCommunicationRepository.save(any(QuestioningCommunication.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    questioningCommunicationService.postQuestioningCommunication("COURRIER_CNRAR", inputDto);

    ArgumentCaptor<QuestioningCommunication> captor = ArgumentCaptor.forClass(QuestioningCommunication.class);
    verify(questioningCommunicationRepository).save(captor.capture());
    QuestioningCommunication saved = captor.getValue();

    assertEquals(TypeCommunicationEvent.COURRIER_CNRAR, saved.getType());
    assertEquals(StatusCommunication.MANUAL, saved.getStatus());
    assertEquals(date, saved.getDate());
    assertEquals(questioning, saved.getQuestioning());
  }
}
