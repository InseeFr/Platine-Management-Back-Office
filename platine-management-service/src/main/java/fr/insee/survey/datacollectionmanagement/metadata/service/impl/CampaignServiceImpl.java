package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.dto.*;
import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.ParametersService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

  private final CampaignRepository campaignRepository;

  private final PartitioningService partitioningService;

  private final ParametersService parametersService;

  private final ModelMapper modelmapper;

  public Collection<CampaignMoogDto> getCampaigns() {

    List<CampaignMoogDto> moogCampaigns = new ArrayList<>();
    List<Campaign> campaigns = campaignRepository.findAll().stream()
        .filter(c -> !c.getPartitionings().isEmpty()).toList();

    for (Campaign campaign : campaigns) {
      CampaignMoogDto campaignMoogDto = new CampaignMoogDto();
      campaignMoogDto.setId(campaign.getId());
      campaignMoogDto.setLabel(campaign.getCampaignWording());

      Optional<Date> dateMin = campaign.getPartitionings().stream()
          .map(Partitioning::getOpeningDate)
          .min(Comparator.comparing(Date::getTime));
      Optional<Date> dateMax = campaign.getPartitionings().stream()
          .map(Partitioning::getClosingDate)
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
    return campaignRepository.findById(idCampaign).orElseThrow(
        () -> new NotFoundException(String.format("Campaign %s not found", idCampaign)));
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
  public boolean isCampaignOngoing(String campaignId) {
      return campaignRepository.findById(campaignId)
              .map(campaign -> {
                  Instant now = Instant.now();
                  Set<Partitioning> partitionings = campaign.getPartitionings();
                  if (partitionings != null) {
                      return partitionings
                              .stream()
                              .anyMatch(part -> partitioningService.isOnGoing(part, now));
                  }
                  return false;
              })
              .orElse(false);
  }

  @Override
  public List<CampaignOngoingDto> getCampaignOngoingDtos() {
      return campaignRepository.findAll().stream()
              .filter(campaign -> isCampaignOngoing(campaign.getId()))
              .map(this::convertToCampaignOngoingDto).toList();
  }

  @Override
  public List<ParamsDto> saveParameterForCampaign(Campaign campaign, ParamsDto paramsDto) {
    Parameters param = parametersService.convertToEntity(paramsDto);
    param.setMetadataId(StringUtils.upperCase(campaign.getId()));
    Set<Parameters> updatedParams = parametersService.updateCampaignParams(campaign, param);
    campaign.setParams(updatedParams);
    insertOrUpdateCampaign(campaign);
    return updatedParams.stream().map(parametersService::convertToDto).toList();
  }

  private CampaignOngoingDto convertToCampaignOngoingDto(Campaign campaign) {
    CampaignOngoingDto result = modelmapper.map(campaign, CampaignOngoingDto.class);
    result.setSourceId(campaign.getSurvey().getSource().getId());
    return result;
  }


    @Override
    public Page<CampaignSummaryDto> searchCampaigns(String searchParam, PageRequest of) {
        Page<Campaign> campaigns = campaignRepository.findBySource(searchParam, of);
        if (campaigns.isEmpty()) {
            return Page.empty();
        }
        return campaigns.map(this::convertToCampaignSummaryDto);
    }

    @Override
    public CampaignHeaderDto findCampaignHeaderById(String id) {
        Campaign campaign = findById(id);
        return convertToCampaignHeaderDto(campaign);
    }

    private CampaignHeaderDto convertToCampaignHeaderDto(Campaign c) {
        CampaignHeaderDto campaignHeaderDto = new CampaignHeaderDto();
        campaignHeaderDto.setCampaignId(c.getId());
        campaignHeaderDto.setWording(c.getCampaignWording());
        campaignHeaderDto.setSource(getSourceFromCampaign(c));
        campaignHeaderDto.setPeriod(c.getPeriod().getValue());
        campaignHeaderDto.setYear(c.getYear());
        campaignHeaderDto.setStatus(getCollectionStatus(c));
        return campaignHeaderDto;
    }

    private CampaignSummaryDto convertToCampaignSummaryDto(Campaign c) {
        CampaignSummaryDto campaignSummaryDto = new CampaignSummaryDto();
        campaignSummaryDto.setCampaignId(c.getId());
        campaignSummaryDto.setSource(getSourceFromCampaign(c));
        campaignSummaryDto.setYear(c.getYear());
        campaignSummaryDto.setPeriod(c.getPeriod().getValue());
        campaignSummaryDto.setStatus(getCollectionStatus(c));
        Date openingDate = getEarliestOpeningDate(c.getPartitionings());
        Date closingDate = getLatestClosingDate(c.getPartitionings());
        campaignSummaryDto.setOpeningDate(openingDate);
        campaignSummaryDto.setClosingDate(closingDate);
        return campaignSummaryDto;
    }

    private String getSourceFromCampaign(Campaign c) {
        return Optional.ofNullable(c.getSurvey())
                .map(Survey::getSource)
                .map(Source::getId)
                .orElse(null);
    }

    private Date getEarliestOpeningDate(Set<Partitioning> partitionings) {
        if (partitionings == null) {
            return null;
        }
        return partitionings.stream()
                .map(Partitioning::getOpeningDate)
                .filter(Objects::nonNull)
                .min(Comparator.comparingLong(Date::getTime))
                .orElse(null);
    }

    private Date getLatestClosingDate(Set<Partitioning> partitionings) {
        if (partitionings == null) {
            return null;
        }
        return partitionings.stream()
                .map(Partitioning::getClosingDate)
                .filter(Objects::nonNull)
                .max(Comparator.comparingLong(Date::getTime))
                .orElse(null);
    }

    private CollectionStatus getCollectionStatus(Campaign c) {

        if (c.getPartitionings() == null || c.getPartitionings().isEmpty()) {
            return CollectionStatus.UNDEFINED;
        }
        if (isCampaignOngoing(c.getId())) {
            return CollectionStatus.OPEN;
        }
        return CollectionStatus.CLOSED;
    }

}
