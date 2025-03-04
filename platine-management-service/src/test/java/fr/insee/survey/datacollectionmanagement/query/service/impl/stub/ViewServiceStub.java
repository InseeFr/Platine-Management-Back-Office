package fr.insee.survey.datacollectionmanagement.query.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.Setter;

import java.util.*;

public class ViewServiceStub implements ViewService {
    @Setter
    private Long countViewByIdentifier;
    private Map<String, List<String>> identifiersBySurveyUnit = new HashMap<>();
    private Map<String, Set<String>> campaignsByIdentifiers = new HashMap<>();

    public void setIdentifiersByIdSu(String idSu, List<String> identifiers) {
        identifiersBySurveyUnit.put(idSu, identifiers);
    }

    public void setCampaignsByIdentifiers(Map<String, Set<String>> campaigns) {
        this.campaignsByIdentifiers = campaigns;
    }

    public void addCampaignForContact(String contactId, String campaignId) {
        campaignsByIdentifiers.computeIfAbsent(contactId, k -> new HashSet<>()).add(campaignId);
    }

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
        return new ArrayList<>(campaignsByIdentifiers.getOrDefault(identifier, Collections.emptySet()));
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

    @Override
    public List<String> findIdentifiersByIdSu(String id) {
        return identifiersBySurveyUnit.getOrDefault(id, Collections.emptyList());
    }

    @Override
    public Map<String, Set<String>> findDistinctCampaignByIdentifiers(List<String> identifiers) {
        Map<String, Set<String>> result = new HashMap<>();
        for (String identifier : identifiers) {
            result.put(identifier, campaignsByIdentifiers.getOrDefault(identifier, Collections.emptySet()));
        }
        return result;
    }
}
