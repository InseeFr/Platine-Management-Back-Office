package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommunicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestioningCommunicationControllerTest {

  @Mock
  private QuestioningCommunicationService questioningCommunicationService;

  @InjectMocks
  private QuestioningCommunicationController questioningCommunicationController;

  private UUID questioningId;

  @BeforeEach
  void setUp() {
    questioningId = UUID.randomUUID();
  }

  @Test
  void shouldReturnCreatedWhenCommunicationIsPosted() {
    // Given
    QuestioningCommunicationInputDto inputDto = new QuestioningCommunicationInputDto(
        questioningId,
        StatusCommunication.MANUAL,
        TypeCommunicationEvent.COURRIER_MED
    );

    // When
    ResponseEntity<Void> response = questioningCommunicationController
        .createQuestioningCommunication(inputDto);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    verify(questioningCommunicationService, times(1))
        .postQuestioningCommunication(inputDto);
  }
}
