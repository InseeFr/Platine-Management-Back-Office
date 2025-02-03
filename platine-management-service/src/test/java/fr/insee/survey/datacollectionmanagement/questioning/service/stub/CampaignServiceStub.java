package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignOngoingDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignSummaryDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

public class CampaignServiceStub implements CampaignService {
    @Override
    public Collection<CampaignMoogDto> getCampaigns() {
        return List.of();
    }

    @Override
    public Campaign findById(String idCampaign) {
        Campaign campaign = new Campaign();
        campaign.setId(idCampaign);
        Partitioning partitioning = new Partitioning();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        Date yesterday =calendar.getTime();
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
    public boolean isCampaignOngoing(Campaign campaign) {
        return false;
    }

    @Override
    public List<CampaignOngoingDto> getCampaignOngoingDtos(String campaignType) {
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
}
