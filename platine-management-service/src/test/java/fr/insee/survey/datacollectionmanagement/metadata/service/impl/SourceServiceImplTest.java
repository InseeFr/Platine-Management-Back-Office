package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SourceDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.SourceRepositoryStub;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.SurveyServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.ParametersServiceStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SourceServiceImplTest {

    private final ModelMapper modelMapper = new ModelMapper();
    private final SourceRepositoryStub sourceRepositoryStub = new SourceRepositoryStub();
    private final SurveyServiceStub surveyServiceStub = new SurveyServiceStub();
    private final ParametersServiceStub parametersServiceStub = new ParametersServiceStub();
    private final SourceServiceImpl sourceService = new SourceServiceImpl(modelMapper, sourceRepositoryStub, surveyServiceStub, parametersServiceStub );

    @Test
    @DisplayName("Should return Source when Source exists")
    void shouldReturnSourceWhenSourceExists() {
        // Given
        Source source = new Source();
        source.setId("source-1");
        sourceRepositoryStub.setSources(List.of(source));

        // When
        Source result = sourceService.findById("source-1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("source-1");
    }

    @Test
    @DisplayName("Should throw NotFoundException when Source does not exist")
    void shouldThrowNotFoundExceptionWhenSourceDoesNotExist() {
        assertThrows(NotFoundException.class, () -> sourceService.findById("source-1"));
    }

    @Test
    @DisplayName("Should return list Sources")
    void shouldReturnListSources() {
        // Given
        Source source1 = new Source();
        Source source2 = new Source();
        List<Source> listSources = List.of(source1, source2);
        sourceRepositoryStub.setSources(listSources);

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
        sourceRepositoryStub.setSources(new ArrayList<>(List.of(existingSource)));

        Source newSource = new Source();
        newSource.setId("source-1");

        // When
        Source result = sourceService.insertOrUpdateSource(newSource);

        // Then
        assertThat(sourceRepositoryStub.getSources()).isNotNull().hasSize(1);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("source-1");
        assertThat(result.getSurveys()).hasSize(1);
    }

    @Test
    @DisplayName("Should create a new Source")
    void shouldCreateNewSource() {
        // Given
        Source existingSource = new Source();
        existingSource.setId("source-1");
        sourceRepositoryStub.setSources(new ArrayList<>(List.of(existingSource)));
        Source newSource = new Source();
        newSource.setId("source-2");

        // When
        Source result = sourceService.insertOrUpdateSource(newSource);

        // Then
        assertThat(sourceRepositoryStub.getSources()).isNotNull().hasSize(2);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("source-2");
    }

    @Test
    @DisplayName("Should delete Source by ID")
    void shouldDeleteSourceById() {
        // Given
        Source existingSource = new Source();
        existingSource.setId("source-1");
        sourceRepositoryStub.setSources(new ArrayList<>(List.of(existingSource)));


        // When
        sourceService.deleteSourceById("source-1");

        // Then
        assertThat(sourceRepositoryStub.getSources()).isEmpty();
    }


    @Test
    @DisplayName("Should return an empty list when there are no sources")
    void testGetOngoingSources_whenNoSources() {
        List<SourceDto> result = sourceService.getOngoingSources();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return an empty list when no surveys are ongoing")
    void testGetOngoingSources_whenNoOngoingSurveys() {
        Source source = new Source();
        source.setId("MMM");
        Survey survey = new Survey();
        survey.setId("MMM2025");
        source.setSurveys(Set.of(survey));
        sourceRepositoryStub.setSources(List.of(source));

        List<SourceDto> result = sourceService.getOngoingSources();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return a list of sources with at least one ongoing survey")
    void testGetOngoingSources_whenAtLeastOneSurveyIsOngoing() {
        Source source = new Source();
        source.setId("MMM");
        Survey survey = new Survey();
        survey.setId("MMM2025");
        source.setSurveys(Set.of(survey));
        sourceRepositoryStub.setSources(List.of(source));

        Source source2 = new Source();
        source2.setId("NNN");
        Survey survey2 = new Survey();
        survey2.setId("ONGOING");
        source2.setSurveys(Set.of(survey2));
        sourceRepositoryStub.setSources(List.of(source2));

        List<SourceDto> result = sourceService.getOngoingSources();

        assertEquals(1, result.size());
        assertEquals("NNN", result.getFirst().getId()); // Only source2 should be returned
    }

}
