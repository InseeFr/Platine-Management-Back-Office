package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignOngoingDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface CampaignService {
    
    Collection<CampaignMoogDto> getCampaigns();

    Campaign findById(String idCampaign);

    Page<Campaign> findAll(Pageable pageable);

    List<Campaign> findAll();

    Campaign insertOrUpdateCampaign(Campaign campaign);

    void deleteCampaignById(String id);

    /**
     * Check if a campaign is ongoing, which means checks if all the partitiongs of the campaign are ongoing
     * @param campaign id of the campaign
     * @return true
     */
    boolean isCampaignOngoing(Campaign campaign) ;

    List<CampaignOngoingDto> getCampaignOngoingDtos(String campaignType);

    void saveParameterForCampaign(Campaign campaign, @Valid ParamsDto paramsDto);
}
