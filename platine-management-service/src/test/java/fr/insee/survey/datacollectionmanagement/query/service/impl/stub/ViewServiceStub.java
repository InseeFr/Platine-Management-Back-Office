package fr.insee.survey.datacollectionmanagement.query.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.Setter;

import java.util.List;

public class ViewServiceStub implements ViewService {
    @Setter
    private Long countViewByIdentifier;

    @Override
    public View saveView(View view) {
        return null;
    }

    @Override
    public List<View> findViewByIdentifier(String identifier) {
        return List.of();
    }

    @Override
    public View findFirstViewByIdentifier(String identifier) {
        return null;
    }

    @Override
    public List<View> findViewByCampaignId(String campaignId) {
        return List.of();
    }

    @Override
    public List<String> findDistinctCampaignByIdentifier(String identifier) {
        return List.of();
    }

    @Override
    public List<View> findViewByIdSu(String idSu) {
        return List.of();
    }

    @Override
    public Long countViewByIdentifierIdSuCampaignId(String identifier, String idSu, String campaignId) {
        return countViewByIdentifier;
    }

    @Override
    public List<View> findByIdentifierContainingAndIdSuNotNull(String identifier) {
        return List.of();
    }

    @Override
    public List<View> findViewByIdSuContaining(String field) {
        return List.of();
    }

    @Override
    public View createView(String identifier, String idSu, String campaignId) {
        return null;
    }

    @Override
    public void deleteView(View view) {

    }

    @Override
    public void deleteViewByIdentifier(String identifier) {

    }

    @Override
    public int deleteViewsOfOneCampaign(Campaign campaign) {
        return 0;
    }
}
