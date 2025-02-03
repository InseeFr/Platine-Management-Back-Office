package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SourceServiceImplTest {

    @Mock
    private SourceRepository sourceRepository;

    @InjectMocks
    private SourceServiceImpl sourceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return Source when Source exists")
    void shouldReturnSourceWhenSourceExists() {
        // Given
        Source source = new Source();
        source.setId("source-1");
        when(sourceRepository.findById("source-1")).thenReturn(Optional.of(source));

        // When
        Source result = sourceService.findById("source-1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("source-1");
        verify(sourceRepository, times(1)).findById("source-1");
    }

    @Test
    @DisplayName("Should throw NotFoundException when Source does not exist")
    void shouldThrowNotFoundExceptionWhenSourceDoesNotExist() {
        // Given
        when(sourceRepository.findById("source-1")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> sourceService.findById("source-1"));
        verify(sourceRepository, times(1)).findById("source-1");
    }

    @Test
    @DisplayName("Should return list Sources")
    void shouldReturnListSources() {
        // Given
        Source source1 = new Source();
        Source source2 = new Source();
        List<Source> listSources = List.of(source1, source2);
        when(sourceRepository.findAll()).thenReturn(listSources);

        // When
        List<Source> result = sourceService.findAll();

        // Then
        assertThat(result).isNotNull().hasSize(2);
    }

    @Test
    @DisplayName("Should update an existing Source")
    void shouldUpdateExistingSource() {
        // Given
        Source existingSource = new Source();
        existingSource.setId("source-1");
        existingSource.setSurveys(Set.of(new Survey()));

        Source newSource = new Source();
        newSource.setId("source-1");

        when(sourceRepository.findById("source-1")).thenReturn(Optional.of(existingSource));
        when(sourceRepository.save(newSource)).thenReturn(newSource);

        // When
        Source result = sourceService.insertOrUpdateSource(newSource);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("source-1");
        assertThat(result.getSurveys()).hasSize(1);
        verify(sourceRepository, times(1)).save(newSource);
    }

    @Test
    @DisplayName("Should create a new Source")
    void shouldCreateNewSource() {
        // Given
        Source newSource = new Source();
        newSource.setId("source-2");

        when(sourceRepository.findById("source-2")).thenThrow(NotFoundException.class);
        when(sourceRepository.save(newSource)).thenReturn(newSource);

        // When
        Source result = sourceService.insertOrUpdateSource(newSource);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("source-2");
        verify(sourceRepository, times(1)).save(newSource);
    }

    @Test
    @DisplayName("Should delete Source by ID")
    void shouldDeleteSourceById() {
        // Given
        String sourceId = "source-1";

        doNothing().when(sourceRepository).deleteById(sourceId);

        // When
        sourceService.deleteSourceById(sourceId);

        // Then
        verify(sourceRepository, times(1)).deleteById(sourceId);
    }


}
