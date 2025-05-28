package fr.insee.survey.datacollectionmanagement.query.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
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

    ArrayList<View> views = new ArrayList<>();

    public void setIdentifiersByIdSu(String idSu, List<String> identifiers) {
        identifiersBySurveyUnit.put(idSu, identifiers);
    }

    public void setCampaignsByIdentifiers(Map<String, Set<String>> campaigns) {
        this.campaignsByIdentifiers = campaigns;
    }

    public void addCampaignForContact(String contactId, String campaignId) {
        campaignsByIdentifiers.computeIfAbsent(contactId, k -> new HashSet<>()).add(campaignId);
    }

    private View findViewById(Long id)
    {
        return views.stream().filter(v -> v.getId().equals(id)).findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("No View found with id %s", id)));
    }

    @Override
    public View saveView(View view) {
        View alreadyExistingView;
        try {
            if(view.getId() != null)
            {
                alreadyExistingView = findViewById(view.getId());
                deleteView(alreadyExistingView);
            }
        }
        catch (NotFoundException e)
        {
            // not used
        }

        views.add(view);
        return view;
    }

    @Override
    public List<View> findViewByIdentifier(String identifier) {
        return views.stream().filter(v ->
                v.getIdentifier().equals(identifier)).toList();
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
        return List.copyOf(campaignsByIdentifiers.getOrDefault(identifier, Collections.emptySet()));
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
    public View createViewAndDeleteEmptyExistingOnesByIdentifier(String identifier, String idSu, String campaignId) {
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
    public void deleteView(View view) {
        views.remove(findViewById(view.getId()));
    }

    @Override
    public void deleteViewByIdentifier(String identifier) {
        // Stub
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

    @Override
    public Optional<View>  findByIdentifierAndIdSuAndCampaignId(String contactId, String idSu, String campaignId) {
        return views.stream().filter(v -> v.getCampaignId().equals(campaignId)
                && v.getIdentifier().equals(contactId)
                && v.getIdSu().equals(idSu)).findFirst();
    }
}
