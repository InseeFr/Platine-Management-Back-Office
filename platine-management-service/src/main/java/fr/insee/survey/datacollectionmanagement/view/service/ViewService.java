package fr.insee.survey.datacollectionmanagement.view.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface ViewService {

    View saveView(View view);

    List<View> findViewByIdentifier(String identifier);

    View findFirstViewByIdentifier(String identifier);

    List<View> findViewByCampaignId(String campaignId);

    List<String> findDistinctCampaignByIdentifier(String identifier);

    List<View> findViewByIdSu(String idSu);

    Long countViewByIdentifierIdSuCampaignId(String identifier, String idSu, String campaignId);

    List<View> findByIdentifierContainingAndIdSuNotNull(String identifier);

    List<View> findViewByIdSuContaining(String field);

    View createView(String identifier, String idSu, String campaignId);

    void deleteView(View view);

    void deleteViewByIdentifier(String identifier);

    int deleteViewsOfOneCampaign(Campaign campaign);

    List<String> findIdentifiersByIdSu(String id);

    Map<String, Set<String>> findDistinctCampaignByIdentifiers(List<String> identifiers);
}
