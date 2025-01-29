package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;

    private final PartitioningService partitioningService;

    public Collection<CampaignMoogDto> getCampaigns() {

        List<CampaignMoogDto> moogCampaigns = new ArrayList<>();
        List<Campaign> campaigns = campaignRepository.findAll().stream().filter(c -> !c.getPartitionings().isEmpty()).toList();

        for (Campaign campaign : campaigns) {
            CampaignMoogDto campaignMoogDto = new CampaignMoogDto();
            campaignMoogDto.setId(campaign.getId());
            campaignMoogDto.setLabel(campaign.getCampaignWording());

            Optional<Date> dateMin = campaign.getPartitionings().stream().map(Partitioning::getOpeningDate)
                    .min(Comparator.comparing(Date::getTime));
            Optional<Date> dateMax = campaign.getPartitionings().stream().map(Partitioning::getClosingDate)
                    .max(Comparator.comparing(Date::getTime));

            if (dateMin.isPresent() && dateMax.isPresent()) {
                campaignMoogDto.setCollectionStartDate(dateMin.get().getTime());
                campaignMoogDto.setCollectionEndDate(dateMax.get().getTime());
                moogCampaigns.add(campaignMoogDto);
            } else {
                log.warn("No start date or end date found for campaign {}", campaign.getId());
            }
        }
        return moogCampaigns;
    }

    @Override
    public Campaign findById(String idCampaign) {
        return campaignRepository.findById(idCampaign).orElseThrow(() -> new NotFoundException(String.format("Campaign %s not found", idCampaign)));
    }



    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return campaignRepository.findAll(pageable);
    }

    @Override
    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }

    @Override
    public Campaign insertOrUpdateCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);

    }

    @Override
    public void deleteCampaignById(String id) {
        campaignRepository.deleteById(id);
    }



    @Override
    public boolean isCampaignOngoing(String idCampaign)  {
        Campaign camp = findById(idCampaign);

        Date now = new Date();
        return camp.getPartitionings().stream().anyMatch(part -> partitioningService.isOnGoing(part, now));
    }


}
