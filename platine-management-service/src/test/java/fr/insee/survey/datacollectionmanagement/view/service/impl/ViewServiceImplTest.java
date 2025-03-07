package fr.insee.survey.datacollectionmanagement.view.service.impl;

import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.repository.ViewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViewServiceImplTest {

    @Mock
    private ViewRepository viewRepository;

    @InjectMocks
    private ViewServiceImpl viewService;

    @BeforeEach
    void setUp() {
        reset(viewRepository);
    }

    private View createView(String contactId, String campaignId, String surveyUnitId) {
        View view = new View();
        view.setIdentifier(contactId);
        view.setCampaignId(campaignId);
        view.setIdSu(surveyUnitId);
        return view;
    }


    @Test
    void findIdentifiersByIdSu_shouldReturnEmptyList_whenNoViewsFound() {
        String idSu = "su1";
        when(viewRepository.findByIdSu(idSu)).thenReturn(Collections.emptyList());

        List<String> result = viewService.findIdentifiersByIdSu(idSu);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findIdentifiersByIdSu_shouldReturnIdentifiers_whenViewsExist() {
        String idSu = "su1";
        List<View> views = List.of(
                createView("id1", "campaignA", idSu),
                createView("id2", "campaignA", idSu),
                createView("id3", "campaignA", idSu)
        );

        when(viewRepository.findByIdSu(idSu)).thenReturn(views);

        List<String> result = viewService.findIdentifiersByIdSu(idSu);

        assertEquals(List.of("id1", "id2", "id3"), result);
    }

    @Test
    void findDistinctCampaignByIdentifiers_shouldReturnEmptyMap_whenIdentifiersIsNull() {
        Map<String, Set<String>> result = viewService.findDistinctCampaignByIdentifiers(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findDistinctCampaignByIdentifiers_shouldReturnEmptyMap_whenIdentifiersIsEmpty() {
        Map<String, Set<String>> result = viewService.findDistinctCampaignByIdentifiers(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findDistinctCampaignByIdentifiers_shouldReturnCorrectMapping() {
        List<String> identifiers = List.of("id1", "id2");
        List<View> views = List.of(
                createView("id1", "campaignA", "su1"),
                createView("id2", "campaignB", "su2")
        );

        when(viewRepository.findByIdentifierIn(identifiers)).thenReturn(views);

        Map<String, Set<String>> result = viewService.findDistinctCampaignByIdentifiers(identifiers);

        assertEquals(Set.of("campaignA"), result.get("id1"));
        assertEquals(Set.of("campaignB"), result.get("id2"));
    }

    @Test
    void findDistinctCampaignByIdentifiers_shouldReturnCorrectMapping_whenIdentifierLinkedToMultipleCampaign() {
        List<String> identifiers = List.of("id1");
        List<View> views = List.of(
                createView("id1", "campaignA", "su1"),
                createView("id1", "campaignB", "su2")
        );

        when(viewRepository.findByIdentifierIn(identifiers)).thenReturn(views);

        Map<String, Set<String>> result = viewService.findDistinctCampaignByIdentifiers(identifiers);

        assertEquals(Set.of("campaignA", "campaignB"), result.get("id1"));
    }
}

