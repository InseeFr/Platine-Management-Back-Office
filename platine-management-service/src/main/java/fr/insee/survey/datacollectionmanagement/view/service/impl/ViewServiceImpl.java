package fr.insee.survey.datacollectionmanagement.view.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.repository.ViewRepository;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewServiceImpl implements ViewService {

    private final ViewRepository viewRepository;

    @Override
    public List<View> findViewByIdentifier(String identifier) {
        return viewRepository.findByIdentifier(identifier);
    }

    @Override
    public View findFirstViewByIdentifier(String identifier) {
        return viewRepository.findFirstByIdentifier(identifier);
    }

    @Override
    public List<View> findByIdentifierContainingAndIdSuNotNull(String identifier) {
        return viewRepository.findByIdentifierContainingAndIdSuNotNull(identifier);
    }

    @Override
    public List<View> findViewByCampaignId(String campaignId) {
        return viewRepository.findDistinctViewByCampaignId(campaignId);
    }

    @Override
    public List<String> findDistinctCampaignByIdentifier(String identifier) {
        return viewRepository.findDistinctCampaignByIdentifier(identifier);
    }



    @Override
    public List<View> findViewByIdSu(String idSu) {
        return viewRepository.findByIdSu(idSu);
    }

    @Override
    public List<View> findViewByIdSuContaining(String field) {
        return viewRepository.findByIdSuContaining(field);
    }
    
    @Override
    public Long countViewByIdentifierIdSuCampaignId(String identifier, String idSu, String campaignId) {
        return viewRepository.countViewByIdentifierAndIdSuAndCampaignId(identifier, idSu, campaignId);
    }

    @Override
    public View saveView(View view) {
        return viewRepository.save(view);
    }

    @Override
    public void deleteView(View view) {
        viewRepository.delete(view);
    }

    @Override
    public void deleteViewByIdentifier(String identifier) {
        viewRepository.deleteByIdentifier(identifier);
    }

    @Override
    public View createView(String identifier, String idSu, String campaignId) {
        View view = new View();
        view.setIdentifier(identifier);
        view.setCampaignId(campaignId);
        view.setIdSu(idSu);
        List<View> listContactView = findViewByIdentifier(identifier);
        listContactView.forEach(v -> {
            if (v.getIdSu() == null)
                deleteView(v);
        });
        return saveView(view);
    }

    @Override
    public int deleteViewsOfOneCampaign(Campaign campaign) {
        List<View> listtView = findViewByCampaignId(campaign.getId());
        listtView
                .forEach(this::deleteView);
        return listtView.size();
    }

}
