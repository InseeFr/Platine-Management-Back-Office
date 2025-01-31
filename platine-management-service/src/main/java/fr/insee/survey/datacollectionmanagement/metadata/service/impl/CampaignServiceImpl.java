package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import static fr.insee.survey.datacollectionmanagement.metadata.enums.UrlTypeEnum.V3;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignOngoingDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SensitivityEnum;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.ParametersService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
  public boolean isCampaignOngoing(Campaign campaign) {
    Date now = new Date();
    if (campaign.getPartitionings() != null) {
      return campaign.getPartitionings().stream()
          .anyMatch(part -> partitioningService.isOnGoing(part, now));
    }
    return false;
  }

  @Override
  public List<CampaignOngoingDto> getCampaignOngoingDtos(String campaignType) {
    List<Campaign> listCampaigns = findAll();
    return listCampaigns.stream().filter(c -> isCampaignOngoing(c)
            && isCampaignInType(c, campaignType)).
        map(this::convertToCampaignOngoingDto).toList();


  }

  @Override
  public void saveParameterForCampaign(Campaign campaign, ParamsDto paramsDto) {
    Parameters param = parametersService.convertToEntity(paramsDto);
    param.setMetadataId(StringUtils.upperCase(campaign.getId()));
    Set<Parameters> updatedParams = parametersService.updateCampaignParams(campaign, param);
    campaign.setParams(updatedParams);
    insertOrUpdateCampaign(campaign);
  }

  private CampaignOngoingDto convertToCampaignOngoingDto(Campaign campaign) {
    CampaignOngoingDto result = modelmapper.map(campaign, CampaignOngoingDto.class);
    result.setSourceId(campaign.getSurvey().getSource().getId());
    Optional<String> paramSensitivity = campaign.getParams().stream()
        .filter(parameters -> parameters.getParamId().equals(ParameterEnum.SENSITIVITY))
        .map(Parameters::getParamValue)
        .findFirst();
    result.setSensitivity(paramSensitivity.orElse(SensitivityEnum.NORMAL.name()));
    return result;
  }

  boolean isCampaignInType(Campaign c, String campaignType) {
    if (StringUtils.isEmpty(campaignType)) {
      return true;
    }
    if (campaignType.equalsIgnoreCase(V3.name())) {
      return parametersService.findSuitableParameterValue(c, ParameterEnum.URL_TYPE)
          .equalsIgnoreCase(campaignType)
          || parametersService.findSuitableParameterValue(c, ParameterEnum.URL_TYPE).isEmpty();
    }
    return parametersService.findSuitableParameterValue(c, ParameterEnum.URL_TYPE)
        .equalsIgnoreCase(campaignType);
  }


}
