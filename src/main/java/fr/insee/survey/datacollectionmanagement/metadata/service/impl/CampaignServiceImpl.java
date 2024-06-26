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
    public List<Campaign> findbyPeriod(String period) {
        return campaignRepository.findByPeriod(period);
    }

    @Override
    public Campaign findById(String idCampaign) {
        return campaignRepository.findById(idCampaign).orElseThrow(() -> new NotFoundException(String.format("Campaign %s not found", idCampaign)));
    }

    @Override
    public List<Campaign> findbySourceYearPeriod(String source, Integer year, String period) {
        return campaignRepository.findBySourceYearPeriod(source, year, period);
    }

    @Override
    public List<Campaign> findbySourcePeriod(String source, String period) {
        return campaignRepository.findBySourcePeriod(source, period);
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return campaignRepository.findAll(pageable);
    }

    @Override
    public Campaign insertOrUpdateCampaign(Campaign campaign) {
        try {
            Campaign campaignBase = findById(campaign.getId());
            log.info("Update campaign with the id {}", campaign.getId());
            campaign.setPartitionings(campaignBase.getPartitionings());

        } catch (NotFoundException e) {
            log.info("Create campaign with the id {}", campaign.getId());
        }
        return campaignRepository.save(campaign);

    }

    @Override
    public void deleteCampaignById(String id) {
        campaignRepository.deleteById(id);
    }

    @Override
    public Campaign addPartitionigToCampaign(Campaign campaign, Partitioning partitioning) {
        Set<Partitioning> partitionings;
        try {
            Campaign campaignBase = findById(campaign.getId());
            partitionings = campaignBase.getPartitionings();
            if (!isPartitioningPresent(partitioning, campaignBase))
                partitionings.add(partitioning);
        } catch (NotFoundException e) {
            partitionings = Set.of(partitioning);
        }
        campaign.setPartitionings(partitionings);
        return campaign;
    }

    private boolean isPartitioningPresent(Partitioning p, Campaign c) {
        for (Partitioning part : c.getPartitionings()) {
            if (part.getId().equals(p.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCampaignOngoing(String idCampaign)  {
        Campaign camp = findById(idCampaign);

        Date now = new Date();
        return camp.getPartitionings().stream().anyMatch(part -> isPartitionOngoing(part, now));
    }

    private boolean isPartitionOngoing (Partitioning part, Date now) {

        boolean ongoing = partitioningService.isOnGoing(part, now);
        log.info("Partitioning {} of campaign {} is {}", part.getId(), part.getCampaign().getId(), ongoing ? "ongoing" : "closed");
        return ongoing;
    }
}
