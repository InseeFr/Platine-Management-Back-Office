package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestioningCommunicationServiceImplTest {

    @Mock
    private QuestioningRepository questioningRepository;

    private ModelMapper modelMapper = new ModelMapper();

    private QuestioningCommunicationServiceImpl questioningCommunicationService;

    @BeforeEach
    void setUp() {
        questioningCommunicationService = new QuestioningCommunicationServiceImpl(modelMapper, questioningRepository);
    }

    @Test
    void shouldReturnDtoListWhenQuestioningExistsWithCommunications() {
        // Given
        Long questioningId = 1L;
        Questioning questioning = new Questioning();
        QuestioningCommunication communication1 = new QuestioningCommunication();
        QuestioningCommunication communication2 = new QuestioningCommunication();
        questioning.setQuestioningCommunications(Set.of(communication1, communication2));


        when(questioningRepository.findById(questioningId)).thenReturn(Optional.of(questioning));

        // When
        List<QuestioningCommunicationDto> result = questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(questioningId);

        // Then
        assertThat(result).isNotNull();
        assertEquals(2, result.size());
        assertThat(result.getFirst()).isNotNull();
        assertThat(result.get(1)).isNotNull();
    }

    @Test
    void shouldReturnEmptyListWhenQuestioningDoesNotExist() {
        // Given
        Long questioningId = 2L;
        when(questioningRepository.findById(questioningId)).thenReturn(Optional.empty());

        // When
        List<QuestioningCommunicationDto> result = questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(questioningId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenQuestioningExistsButHasNoCommunications() {
        // Given
        Long questioningId = 3L;
        Questioning questioning = new Questioning();
        questioning.setQuestioningCommunications(Set.of());

        when(questioningRepository.findById(questioningId)).thenReturn(Optional.of(questioning));

        // When
        List<QuestioningCommunicationDto> result = questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(questioningId);

        // Then
        assertTrue(result.isEmpty());
    }

}
