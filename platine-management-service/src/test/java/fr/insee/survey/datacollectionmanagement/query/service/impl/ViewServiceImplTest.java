package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.ViewRepositoryStub;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.repository.ViewRepository;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import fr.insee.survey.datacollectionmanagement.view.service.impl.ViewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ViewServiceImplTest {

    private ViewService viewService;
    private ViewRepository viewRepository;

    @BeforeEach
    void setUp() {

        viewRepository = new ViewRepositoryStub();
        viewService = new ViewServiceImpl(viewRepository);
    }

    @Test
    @DisplayName("Should find an Id by Contact Identifier, Campaign Id and Survey Unit Id")
    void findByIdentifierAndIdSuAndCampaignIdTest1() {
        String identifier = "id";
        String campaignId = "campaign-id";
        String idSu = "id-su";

        View viewToFind = new View();
        viewToFind.setId(1L);
        viewToFind.setIdentifier(identifier);
        viewToFind.setCampaignId(campaignId);
        viewToFind.setIdSu(idSu);
        viewRepository.save(viewToFind);

        List<View> foundViews = viewService.findByIdentifierAndIdSuAndCampaignId(identifier, idSu, campaignId);
        assertThat(foundViews).hasSize(1);
        View foundView = foundViews.getFirst();
        assertThat(foundView.getId()).isEqualTo(viewToFind.getId());
        assertThat(foundView.getIdentifier()).isEqualTo(viewToFind.getIdentifier());
        assertThat(foundView.getCampaignId()).isEqualTo(viewToFind.getCampaignId());
        assertThat(foundView.getIdSu()).isEqualTo(viewToFind.getIdSu());
    }

    @Test
    @DisplayName("Should not find an Id by Contact Identifier, Campaign Id and and different Survey Unit Id")
    void findByIdentifierAndIdSuAndCampaignIdTest2() {
        String identifier = "id";
        String campaignId = "campaign-id";
        String idSu = "id-su";

        View viewToFind = new View();
        viewToFind.setId(1L);
        viewToFind.setIdentifier(identifier);
        viewToFind.setCampaignId(campaignId);
        viewToFind.setIdSu("id-su2");
        viewRepository.save(viewToFind);

        List<View> foundViews = viewService.findByIdentifierAndIdSuAndCampaignId(identifier, idSu, campaignId);
        assertThat(foundViews).isEmpty();
    }
}