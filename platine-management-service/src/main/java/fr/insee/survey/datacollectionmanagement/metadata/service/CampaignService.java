package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface CampaignService {
    
    Collection<CampaignMoogDto> getCampaigns();

    Campaign findById(String idCampaign);

    Page<Campaign> findAll(Pageable pageable);

    Campaign insertOrUpdateCampaign(Campaign campaign);

    void deleteCampaignById(String id);

    /**
     * Check if a campaign is ongoing, which means checks if all the partitiongs of the campaign are ongoing
     * @param idCampaign id of the campaign
     * @return true
     */
    boolean isCampaignOngoing(String idCampaign) ;

    Page<CampaignSummaryDto> searchCampaigns(String searchParam, PageRequest of);
}
