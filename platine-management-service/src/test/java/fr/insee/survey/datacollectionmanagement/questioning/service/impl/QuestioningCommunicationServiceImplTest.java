package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningCommunicationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        questioningCommunicationService = new QuestioningCommunicationServiceImpl(modelMapper, questioningRepository, questioningCommunicationRepository);
    }

  @Test
  void shouldReturnDtoListWhenQuestioningExistsWithCommunications() {
    // Given
    UUID questioningId = UUID.randomUUID();
    QuestioningCommunication communication1 = new QuestioningCommunication();
    QuestioningCommunication communication2 = new QuestioningCommunication();
    List<QuestioningCommunication> communications = List.of(communication1, communication2);

    when(questioningCommunicationRepository.findByQuestioningId(questioningId)).thenReturn(communications);

    // When
    List<QuestioningCommunicationDto> result =
        questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(questioningId);

    // Then
    assertThat(result).isNotNull();
    assertEquals(2, result.size());
    assertThat(result.getFirst()).isNotNull();
    assertThat(result.get(1)).isNotNull();
  }

  @Test
  void shouldReturnEmptyListWhenNoCommunicationsFound() {
    // Given
    UUID questioningId = UUID.randomUUID();
    when(questioningCommunicationRepository.findByQuestioningId(questioningId)).thenReturn(List.of());

    // When
    List<QuestioningCommunicationDto> result =
        questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(questioningId);

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyListWhenNoCommunicationsFoundForQuestioning() {
    // Given
    UUID questioningId = UUID.randomUUID();
    when(questioningCommunicationRepository.findByQuestioningId(questioningId)).thenReturn(List.of());

    // When
    List<QuestioningCommunicationDto> result =
        questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(questioningId);

    // Then
    assertTrue(result.isEmpty());
  }

}
