package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Owner;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.BusinessMetadataDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignBusinessDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.OwnerBusinessDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SurveyBusinessDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MetadataServiceImpl implements MetadataService {

    public static final String OUI = "oui";
    public static final String NON = "non";
    private final CampaignService campaignService;

    @Override
    public BusinessMetadataDto getBusinessMetadataDtoForCampaign(String campaignId) {
        BusinessMetadataDto businessMetadataDto = new BusinessMetadataDto();

        CampaignBusinessDto campaignBusinessDto = new CampaignBusinessDto();
        Campaign campaign = campaignService.findById(campaignId);
        campaignBusinessDto.setCampaignWording(campaign.getCampaignWording());
        businessMetadataDto.setCampaignBusinessDto(campaignBusinessDto);

        SurveyBusinessDto surveyBusinessDto = getSurveyBusinessDto(campaign);
        businessMetadataDto.setSurveyBusinessDto(surveyBusinessDto);

        OwnerBusinessDto ownerBusinessDto = getOwnerBusinessDto(campaign);
        businessMetadataDto.setOwnerBusinessDto(ownerBusinessDto);

        return businessMetadataDto;
    }

    OwnerBusinessDto getOwnerBusinessDto(Campaign campaign) {
        OwnerBusinessDto ownerBusinessDto = new OwnerBusinessDto();
        Owner owner = campaign.getSurvey().getSource().getOwner();
        ownerBusinessDto.setDeterminer(owner.getDeterminer());
        ownerBusinessDto.setMinistry(owner.getMinistry());
        ownerBusinessDto.setLabel(owner.getLabel());
        return ownerBusinessDto;
    }

    SurveyBusinessDto getSurveyBusinessDto(Campaign campaign) {
        SurveyBusinessDto surveyBusinessDto = new SurveyBusinessDto();
        Survey survey = campaign.getSurvey();
        surveyBusinessDto.setYear(survey.getYear());
        surveyBusinessDto.setCompulsaryNature((Boolean.TRUE.equals(survey.isCompulsoryNature())) ? OUI : NON);
        surveyBusinessDto.setShortObjectives(survey.getShortObjectives());
        surveyBusinessDto.setSurveyStatus(survey.getSurveyStatus());
        surveyBusinessDto.setDiffusionUrl(survey.getDiffusionUrl());
        surveyBusinessDto.setNoticeUrl(survey.getNoticeUrl());
        surveyBusinessDto.setSpecimenUrl(survey.getSpecimenUrl());
        surveyBusinessDto.setVisaNumber(survey.getVisaNumber());
        return surveyBusinessDto;
    }
}
