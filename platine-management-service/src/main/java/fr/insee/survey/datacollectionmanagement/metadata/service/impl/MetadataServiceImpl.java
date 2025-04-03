package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.dto.BusinessMetadataDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignBusinessDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MetadataServiceImpl implements MetadataService {

    private final CampaignService campaignService;

    @Override
    public BusinessMetadataDto getBusinessMetadataDtoForCampaign(String campaignId) {
        BusinessMetadataDto businessMetadataDto = new BusinessMetadataDto();

        CampaignBusinessDto campaignBusinessDto = new CampaignBusinessDto();
        Campaign campaign = campaignService.findById(campaignId);
        campaignBusinessDto.setCampaignWording(campaign.getCampaignWording());
        businessMetadataDto.setCampaignBusinessDto(campaignBusinessDto);

        return businessMetadataDto;
    }
}
