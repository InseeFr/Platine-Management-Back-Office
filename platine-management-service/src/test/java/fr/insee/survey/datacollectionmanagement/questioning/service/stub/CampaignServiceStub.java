package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.*;
import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

public class CampaignServiceStub implements CampaignService {

    private final Map<String, CampaignStatusDto> campaignStatusMap = new HashMap<>();

    public void addCampaignStatus(String campaignId, CollectionStatus status) {
        campaignStatusMap.put(campaignId, new CampaignStatusDto(campaignId, status));
    }

    @Override
    public Collection<CampaignMoogDto> getCampaigns() {
        return List.of();
    }

    @Override
    public Campaign findById(String idCampaign) {
        Campaign campaign = new Campaign();
        campaign.setId(idCampaign);
        campaign.setCampaignWording("Test Campaign");
        Partitioning partitioning = new Partitioning();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        Date tomorrow = calendar.getTime();
        partitioning.setOpeningDate(yesterday);
        partitioning.setClosingDate(tomorrow);
        campaign.setPartitionings(Set.of(partitioning));
        return campaign;
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Campaign> findAll() {
        return List.of();
    }

    @Override
    public Campaign insertOrUpdateCampaign(Campaign campaign) {
        return null;
    }

    @Override
    public void deleteCampaignById(String id) {
        // stub method
    }

    @Override
    public boolean isCampaignOngoing(String campaignId) {
        return "ONGOING".equals(campaignId);
    }

    @Override
    public List<CampaignOngoingDto> getCampaignOngoingDtos() {
        return List.of();
    }

    @Override
    public List<ParamsDto> saveParameterForCampaign(Campaign campaign, ParamsDto paramsDto) {
        return null;
    }

    @Override
    public Page<CampaignSummaryDto> searchCampaigns(String searchParam, PageRequest of) {
        return Page.empty();
    }

    @Override
    public CampaignHeaderDto findCampaignHeaderById(String id) {
        return null;
    }

    @Override
    public List<CampaignStatusDto> findCampaignStatusByCampaignIdIn(List<String> ids) {
        return ids.stream()
                .map(campaignStatusMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
