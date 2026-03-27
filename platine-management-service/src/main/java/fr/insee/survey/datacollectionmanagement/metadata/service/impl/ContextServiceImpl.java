package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.*;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SourceTypeEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContextServiceImpl implements ContextService {
    private final SourceService sourceService;
    private final SurveyService surveyService;
    private final CampaignService campaignService;
    private final PartitioningService partitioningService;

    @Override
    public void saveContext(ContextCreateDto context) {
        SourceCreateContextDto sourceDto = context.source();
        Source sourceBase = sourceService.findById(sourceDto.id());
        Source source = toSource(sourceDto, sourceBase.getOwner(), sourceBase.getSupport());
        Source savedSource = sourceService.insertOrUpdateSource(source);
        Survey survey = toSurvey(context.survey(), savedSource);
        Survey savedSurvey = surveyService.insertOrUpdateSurvey(survey);
        Campaign campaignToSave = toCampaign(context.campaign(), savedSurvey);
        Campaign campaign = campaignService.insertOrUpdateCampaign(campaignToSave);

        context.partitionings().forEach(dto -> {
            Partitioning partitioning = toPartitioning(dto);
            partitioning.setCampaign(campaign);
            partitioningService.insertOrUpdatePartitioning(partitioning);
        });
    }


    private Source toSource(SourceCreateContextDto dto, Owner owner, Support support) {
        String personalData = "non";
        if(dto.personalData()) {
            personalData = "oui";
        }
        Source source = new Source();
        source.setId(dto.id());
        source.setType(SourceTypeEnum.valueOf(dto.type()));
        source.setShortWording(dto.shortWording());
        source.setLongWording(dto.longWording());
        source.setPeriodicity(dto.periodicity());
        source.setPersonalData(personalData);
        source.setMandatoryMySurveys(false);
        source.setOwner(owner);
        source.setSupport(support);
        source.setStorageTime("5 ans");
        return source;
    }

    private Survey toSurvey(SurveyCreateContextDto dto, Source source) {
        Survey survey = new Survey();
        survey.setId(dto.id());
        survey.setYear(dto.year());
        survey.setShortWording(dto.shortWording());
        survey.setLongWording(dto.longWording());
        survey.setVisaNumber(dto.visaNumber());
        survey.setCompulsoryNature(dto.compulsoryNature());
        survey.setContactExtraction(false);
        survey.setSviUse(dto.sviUse());
        survey.setLongObjectives(dto.longObjectives());
        survey.setCnisUrl(dto.cnisUrl());
        survey.setDiffusionUrl(dto.diffusionUrl());
        survey.setRgpdBlock(dto.rgpdBlock());
        survey.setSendPaperQuestionnaire(dto.sendPaperQuestionnaire());
        survey.setSurveyStatus(dto.surveyStatus());
        survey.setSviNumber(dto.sviNumber());
        survey.setSource(source);
        return survey;
    }

    private Campaign toCampaign(CampaignCreateContextDto dto, Survey survey) {
        Campaign campaign = new Campaign();
        campaign.setId(dto.id());
        campaign.setTechnicalId(dto.technicalId());
        campaign.setYear(dto.year());
        campaign.setCampaignWording(dto.campaignWording());
        campaign.setPeriod(PeriodEnum.valueOf(dto.period()));
        campaign.setPeriodCollect(PeriodEnum.valueOf(dto.period()));
        campaign.setDataCollectionTarget(DataCollectionEnum.LUNATIC_NORMAL);
        campaign.setSensitivity(false);
        campaign.setSurvey(survey);
        return campaign;
    }

    private Partitioning toPartitioning(PartitioningCreateContextDto dto) {
        Partitioning partitioning = new Partitioning();
        partitioning.setId(dto.id());
        partitioning.setTechnicalId(dto.technicalId());
        partitioning.setLabel(dto.label());
        partitioning.setOpeningDate(dto.openingDate());
        partitioning.setClosingDate(dto.closingDate());
        partitioning.setReturnDate(dto.returnDate());
        partitioning.setOpeningLetterDate(dto.openingLetterDate());
        partitioning.setOpeningMailDate(dto.openingMailDate());
        partitioning.setFollowupLetter1Date(dto.followupLetter1Date());
        partitioning.setFollowupLetter2Date(dto.followupLetter2Date());
        partitioning.setFollowupLetter3Date(dto.followupLetter3Date());
        partitioning.setFollowupLetter4Date(dto.followupLetter4Date());
        partitioning.setFollowupMail1Date(dto.followupMail1Date());
        partitioning.setFollowupMail2Date(dto.followupMail2Date());
        partitioning.setFollowupMail3Date(dto.followupMail3Date());
        partitioning.setFollowupMail4Date(dto.followupMail4Date());
        partitioning.setFormalNoticeDate(dto.formalNoticeDate());
        partitioning.setNoReplyDate(dto.noReplyDate());
        return partitioning;
    }
}
