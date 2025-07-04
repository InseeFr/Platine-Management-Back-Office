package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
     * Check if a campaign is ongoing, which means checks if all the partitiongs of the campaign are
     * ongoing
     *
     * @param campaignId of the campaign
     * @return true
     */
    boolean isCampaignOngoing(String campaignId);

    /**
     * retrieve information from a campaign passed as a parameter
     *
     * @param campaignId of the campaign
     * @return CampaignCommonsOngoingDto
     */
    CampaignCommonsDto findCampaignDtoById(String campaignId);

    List<CampaignOngoingDto> getCampaignOngoingDtos();

    List<CampaignCommonsDto> getCampaignCommonsOngoingDtos();

    List<ParamsDto> saveParameterForCampaign(Campaign campaign, ParamsDto paramsDto);

    Page<CampaignSummaryDto> searchCampaigns(String searchParam, PageRequest of);

    CampaignHeaderDto findCampaignHeaderById(String id);

    List<CampaignStatusDto> findCampaignStatusByCampaignIdIn(List<String> ids);
}
