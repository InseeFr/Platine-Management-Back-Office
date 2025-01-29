package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.query.dto.MoogFollowUpDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogProgressDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogRowProgressDto;
import fr.insee.survey.datacollectionmanagement.query.repository.MonitoringRepository;
import fr.insee.survey.datacollectionmanagement.util.JSONCollectionWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MonitoringServiceImplTest {

    @Mock
    private MonitoringRepository monitoringRepository;

    @InjectMocks
    private MonitoringServiceImpl monitoringService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given progress data, when getProgress is called, then return aggregated results")
    void givenProgressData_whenGetProgress_thenReturnAggregatedResults() {
        // Given
        String idCampaign = "testCampaign";
        List<MoogRowProgressDto> mockRows = Arrays.asList(
                new MoogRowProgressDto(5, "REFUSAL", "1"),
                new MoogRowProgressDto(3, "VALINT", "1"),
                new MoogRowProgressDto(2, "PND", "2")
        );
        when(monitoringRepository.getProgress(idCampaign)).thenReturn(mockRows);

        // When
        JSONCollectionWrapper<MoogProgressDto> result = monitoringService.getProgress(idCampaign);

        // Then
        assertEquals(2, result.getDatas().size());

        MoogProgressDto lot1 = result.getDatas().stream()
                .filter(lot -> lot.getBatchNumber().equals("1"))
                .findFirst()
                .orElse(null);

        MoogProgressDto lot2 = result.getDatas().stream()
                .filter(lot -> lot.getBatchNumber().equals("2"))
                .findFirst()
                .orElse(null);

        assertEquals(8, lot1.getNbSu());
        assertEquals(5, lot1.getNbRefusal());
        assertEquals(3, lot1.getNbIntReceived());

        assertEquals(2, lot2.getNbPND());

        verify(monitoringRepository, times(1)).getProgress(idCampaign);
    }

    @Test
    @DisplayName("Given follow-up data, when getFollowUp is called, then return follow-up results")
    void givenFollowUpData_whenGetFollowUp_thenReturnFollowUpResults() {
        String idCampaign = "testCampaign";

        List<MoogFollowUpDto> mockFollowUp = Arrays.asList(
                new MoogFollowUpDto(10, 5, "FollowUp1"),
                new MoogFollowUpDto(20, 10, "FollowUp2")
        );

        when(monitoringRepository.getFollowUp(idCampaign)).thenReturn(mockFollowUp);

        // When
        JSONCollectionWrapper<MoogFollowUpDto> result = monitoringService.getFollowUp(idCampaign);

        // Then
        assertEquals(2, result.getDatas().size());

        MoogFollowUpDto followUp1 = result.getDatas().stream()
                .filter(fu -> fu.getBatchNum().equals("FollowUp1"))
                .findFirst()
                .orElse(null);

        MoogFollowUpDto followUp2 = result.getDatas().stream()
                .filter(fu -> fu.getBatchNum().equals("FollowUp2"))
                .findFirst()
                .orElse(null);

        assertEquals(10, followUp1.getNb());
        assertEquals(5, followUp1.getFreq());

        assertEquals(20, followUp2.getNb());
        assertEquals(10, followUp2.getFreq());

        verify(monitoringRepository, times(1)).getFollowUp(idCampaign);
    }

}
