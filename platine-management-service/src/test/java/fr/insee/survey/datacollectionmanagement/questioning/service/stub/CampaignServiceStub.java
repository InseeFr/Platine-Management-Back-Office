package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import org.springframework.data.domain.Page;
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
    public List<Campaign> findbyPeriod(String period) {
        return List.of();
    }

    @Override
    public List<Campaign> findbySourceYearPeriod(String source, Integer year, String period) {
        return List.of();
    }

    @Override
    public List<Campaign> findbySourcePeriod(String source, String period) {
        return List.of();
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Campaign insertOrUpdateCampaign(Campaign campaign) {
        return null;
    }

    @Override
    public void deleteCampaignById(String id) {

    }

    @Override
    public Campaign addPartitionigToCampaign(Campaign campaign, Partitioning partitioning) {
        return null;
    }

    @Override
    public boolean isCampaignOngoing(String idCampaign) {
        return false;
    }
}
