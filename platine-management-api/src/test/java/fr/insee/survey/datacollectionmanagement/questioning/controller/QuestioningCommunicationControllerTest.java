package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommunicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Date;
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
  void shouldReturnCreatedWhenPostReturnsTrue() {
    QuestioningCommunicationInputDto inputDto = new QuestioningCommunicationInputDto(
        questioningId,
        new Date(),
        StatusCommunication.MANUAL
    );

    String communicationType = "COURRIER_MED";

    when(questioningCommunicationService.postQuestioningCommunication(communicationType, inputDto))
        .thenReturn(true);

    ResponseEntity<Void> response = questioningCommunicationController
        .createQuestioningCommunication(communicationType, inputDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf((201)));
    verify(questioningCommunicationService, times(1))
        .postQuestioningCommunication(communicationType, inputDto);
  }

  @Test
  void shouldReturnOkWhenPostReturnsFalse() {
    QuestioningCommunicationInputDto inputDto = new QuestioningCommunicationInputDto(
        questioningId,
        new Date(),
        StatusCommunication.AUTOMATIC
    );

    String communicationType = "MAIL_DIVERS";

    when(questioningCommunicationService.postQuestioningCommunication(communicationType, inputDto))
        .thenReturn(false);

    ResponseEntity<Void> response = questioningCommunicationController
        .createQuestioningCommunication(communicationType, inputDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    verify(questioningCommunicationService, times(1))
        .postQuestioningCommunication(communicationType, inputDto);
  }
}
