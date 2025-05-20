package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.repository.ViewRepository;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import fr.insee.survey.datacollectionmanagement.view.service.impl.ViewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ViewServiceImplTest {

    private ViewService viewService;
    private ViewRepository viewRepository;
    private PartitioningService partitioningService;

    @BeforeEach
    void setUp() {
        viewService = new ViewServiceImpl(viewRepository, partitioningService);
    }

    @Test
    @DisplayName("Should update replace view")
    void Test1() {

//        String campaignId = partitioningService.findById(questioningAccreditation.getQuestioning().getIdPartitioning()).getCampaign().getId();
//        String idSu = questioningAccreditation.getQuestioning().getSurveyUnit().getIdSu();
//        View viewToUpdate = findByIdentifierAndIdSuAndCampaignId(questioningAccreditation.getIdContact(), idSu, campaignId);
//        View viewToAdd = new View();
//
//        viewToAdd.setIdentifier(newContactId);
//        viewToAdd.setIdSu(idSu);
//        viewToAdd.setCampaignId(campaignId);
//        viewToUpdate.setIdSu("");
//
//        saveView(viewToAdd);
//        saveView(viewToUpdate);
//        viewService.updateViewForQuestioningAccreditationReplacement();
    }
}