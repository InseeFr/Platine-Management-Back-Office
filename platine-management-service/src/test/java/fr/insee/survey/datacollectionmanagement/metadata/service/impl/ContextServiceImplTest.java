package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.*;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SourceTypeEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.ContextService;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.SourceServiceStub;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.SurveyServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.PartitioningServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ContextServiceImpl#saveContext(ContextCreateDto)}
 */
class ContextServiceImplTest {

    private SourceServiceStub sourceService;
    private SurveyServiceStub surveyService;
    private CampaignServiceStub campaignService;
    private PartitioningServiceStub partitioningService;

    private ContextService contextService; // SUT

    @BeforeEach
    void setUp() {
        sourceService = new SourceServiceStub();
        surveyService = new SurveyServiceStub();
        campaignService = new CampaignServiceStub();
        partitioningService = new PartitioningServiceStub();
        contextService = new ContextServiceImpl(sourceService, surveyService, campaignService, partitioningService);
    }

    @Test
    void saveContext_should_map_and_persist_source_survey_campaign_and_partitionings() {
        // --- Given
        UUID contextTechnicalId = UUID.randomUUID();

        // Semantic date variables for P1
        Date p1OpeningDate         = Date.from(Instant.parse("2025-01-10T00:00:00Z"));
        Date p1ClosingDate         = Date.from(Instant.parse("2025-02-20T00:00:00Z"));
        Date p1ReturnDate          = Date.from(Instant.parse("2025-03-01T00:00:00Z"));
        Date p1OpeningLetterDate   = Date.from(Instant.parse("2025-01-05T00:00:00Z"));
        Date p1OpeningMailDate     = Date.from(Instant.parse("2025-01-06T00:00:00Z"));
        Date p1FollowupLetter1Date = Date.from(Instant.parse("2025-01-20T00:00:00Z"));
        Date p1FollowupLetter2Date = Date.from(Instant.parse("2025-01-27T00:00:00Z"));
        Date p1FollowupMail1Date   = Date.from(Instant.parse("2025-02-03T00:00:00Z"));
        Date p1FollowupMail2Date   = Date.from(Instant.parse("2025-02-05T00:00:00Z"));
        Date p1FormalNoticeDate    = Date.from(Instant.parse("2025-02-10T00:00:00Z"));
        Date p1NoReplyDate         = Date.from(Instant.parse("2025-03-10T00:00:00Z"));

        // Semantic date variables for P2
        Date p2OpeningDate       = Date.from(Instant.parse("2025-04-01T00:00:00Z"));
        Date p2ClosingDate       = Date.from(Instant.parse("2025-04-30T00:00:00Z"));
        Date p2OpeningMailDate   = Date.from(Instant.parse("2025-04-15T00:00:00Z"));

        SourceCreateContextDto sourceDto = new SourceCreateContextDto(
                "SRC_SERIE",
                "HOUSEHOLD",
                "SRC_SERIE",
                "Source long wording",
                PeriodicityEnum.A,
                true
        );

        SurveyCreateContextDto surveyDto = new SurveyCreateContextDto(
                "SURV_OP",
                2025,
                "Source long wording",
                "SRC_SERIE",
                "LOREM LONG",
                "V-999",
                "https://cnis.example",
                "https://diff.example",
                true,
                "RGPD bla bla",
                "Q-42",
                "RUNNING",
                true,
                "SVI-1234"
        );

        CampaignCreateContextDto campaignDto = new CampaignCreateContextDto(
                "CAMP123",
                contextTechnicalId,
                2025,
                "Campaign wording",
                "A00",
                "A00"
        );

        UUID p1TechnicalId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID p2TechnicalId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        PartitioningCreateContextDto p1 = new PartitioningCreateContextDto(
                "P1", p1TechnicalId, "Partition 1",
                p1OpeningDate, p1ClosingDate, p1ReturnDate,
                p1OpeningLetterDate, p1OpeningMailDate,
                p1FollowupLetter1Date, p1FollowupLetter2Date, null, null,
                p1FollowupMail1Date, p1FollowupMail2Date, null, null,
                p1FormalNoticeDate, p1NoReplyDate
        );

        PartitioningCreateContextDto p2 = new PartitioningCreateContextDto(
                "P2", p2TechnicalId, "Partition 2",
                p2OpeningDate, p2ClosingDate, null,
                null, p2OpeningMailDate,
                null, null, null, null,
                null, null, null, null,
                null, null
        );

        ContextCreateDto context = new ContextCreateDto(
                surveyDto, sourceDto, campaignDto, List.of(p1, p2)
        );

        // Seed base source and "persisted" campaign in stubs
        Owner owner = new Owner();
        Support support = new Support();
        Source baseSource = new Source();
        baseSource.setId(sourceDto.id());
        baseSource.setOwner(owner);
        baseSource.setSupport(support);
        sourceService.setSavedSources(List.of(baseSource));

        Campaign persistedCampaign = new Campaign();
        persistedCampaign.setId(campaignDto.id());
        campaignService.setSavedCampaigns(List.of(persistedCampaign));

        // --- When
        contextService.saveContext(context);

        // --- Then
        // Source assertions (use DTO as source of truth)
        Source savedSource = sourceService.getLastSaved();
        assertThat(savedSource.getId()).isEqualTo(sourceDto.id());
        assertThat(savedSource.getType()).isEqualTo(SourceTypeEnum.valueOf(sourceDto.type()));
        assertThat(savedSource.getShortWording()).isEqualTo(sourceDto.shortWording());
        assertThat(savedSource.getLongWording()).isEqualTo(sourceDto.longWording());
        assertThat(savedSource.getPeriodicity()).isEqualTo(sourceDto.periodicity());
        assertThat(savedSource.getPersonalData()).isEqualTo("oui");
        assertThat(savedSource.getMandatoryMySurveys()).isFalse(); // fixed by service
        assertThat(savedSource.getOwner()).isSameAs(owner);
        assertThat(savedSource.getSupport()).isSameAs(support);

        // Survey assertions (use DTO as source of truth)
        Survey savedSurvey = surveyService.getLastSaved();
        assertThat(savedSurvey.getId()).isEqualTo(surveyDto.id());
        assertThat(savedSurvey.getYear()).isEqualTo(surveyDto.year());
        assertThat(savedSurvey.getShortWording()).isEqualTo(surveyDto.shortWording());
        assertThat(savedSurvey.getLongWording()).isEqualTo(surveyDto.longWording());
        assertThat(savedSurvey.getVisaNumber()).isEqualTo(surveyDto.visaNumber());
        assertThat(savedSurvey.isCompulsoryNature()).isEqualTo(surveyDto.compulsoryNature());
        assertThat(savedSurvey.isContactExtraction()).isFalse(); // fixed by service
        assertThat(savedSurvey.isSviUse()).isEqualTo(surveyDto.sviUse());
        assertThat(savedSurvey.getLongObjectives()).isEqualTo(surveyDto.longObjectives());
        assertThat(savedSurvey.getCnisUrl()).isEqualTo(surveyDto.cnisUrl());
        assertThat(savedSurvey.getDiffusionUrl()).isEqualTo(surveyDto.diffusionUrl());
        assertThat(savedSurvey.getRgpdBlock()).isEqualTo(surveyDto.rgpdBlock());
        assertThat(savedSurvey.getSendPaperQuestionnaire()).isEqualTo(surveyDto.sendPaperQuestionnaire());
        assertThat(savedSurvey.getSurveyStatus()).isEqualTo(surveyDto.surveyStatus());
        assertThat(savedSurvey.getSviNumber()).isEqualTo(surveyDto.sviNumber());
        assertThat(savedSurvey.getSource()).isSameAs(savedSource);

        // Campaign assertions (use DTO as source of truth)
        Campaign savedCampaign = campaignService.getLastSaved();
        assertThat(savedCampaign.getId()).isEqualTo(campaignDto.id());
        assertThat(savedCampaign.getTechnicalId()).isEqualTo(campaignDto.technicalId());
        assertThat(savedCampaign.getYear()).isEqualTo(campaignDto.year());
        assertThat(savedCampaign.getCampaignWording()).isEqualTo(campaignDto.campaignWording());
        assertThat(savedCampaign.getPeriod().name()).isEqualTo(campaignDto.period());
        assertThat(savedCampaign.getPeriodCollect().name()).isEqualTo(campaignDto.period());
        assertThat(savedCampaign.getDataCollectionTarget()).isEqualTo(DataCollectionEnum.LUNATIC_NORMAL); // fixed by service
        assertThat(savedCampaign.isSensitivity()).isFalse(); // fixed by service
        assertThat(savedCampaign.getSurvey()).isSameAs(savedSurvey);

        // Partitionings assertions — compare against DTOs (no hardcoded dates)
        var savedPartitionings = partitioningService.getSavedPartitionings();
        assertThat(savedPartitionings).hasSize(2);

        Partitioning savedP1 = savedPartitionings.get(0);
        assertThat(savedP1.getId()).isEqualTo(p1.id());
        assertThat(savedP1.getTechnicalId()).isEqualTo(p1.technicalId());
        assertThat(savedP1.getLabel()).isEqualTo(p1.label());
        assertThat(savedP1.getOpeningDate()).isEqualTo(p1.openingDate());
        assertThat(savedP1.getClosingDate()).isEqualTo(p1.closingDate());
        assertThat(savedP1.getReturnDate()).isEqualTo(p1.returnDate());
        assertThat(savedP1.getOpeningLetterDate()).isEqualTo(p1.openingLetterDate());
        assertThat(savedP1.getOpeningMailDate()).isEqualTo(p1.openingMailDate());
        assertThat(savedP1.getFollowupLetter1Date()).isEqualTo(p1.followupLetter1Date());
        assertThat(savedP1.getFollowupLetter2Date()).isEqualTo(p1.followupLetter2Date());
        assertThat(savedP1.getFollowupLetter3Date()).isEqualTo(p1.followupLetter3Date());
        assertThat(savedP1.getFollowupLetter4Date()).isEqualTo(p1.followupLetter4Date());
        assertThat(savedP1.getFollowupMail1Date()).isEqualTo(p1.followupMail1Date());
        assertThat(savedP1.getFollowupMail2Date()).isEqualTo(p1.followupMail2Date());
        assertThat(savedP1.getFollowupMail3Date()).isEqualTo(p1.followupMail3Date());
        assertThat(savedP1.getFollowupMail4Date()).isEqualTo(p1.followupMail4Date());
        assertThat(savedP1.getFormalNoticeDate()).isEqualTo(p1.formalNoticeDate());
        assertThat(savedP1.getNoReplyDate()).isEqualTo(p1.noReplyDate());
        assertThat(savedP1.getCampaign()).isSameAs(savedCampaign);

        Partitioning savedP2 = savedPartitionings.get(1);
        assertThat(savedP2.getId()).isEqualTo(p2.id());
        assertThat(savedP2.getTechnicalId()).isEqualTo(p2.technicalId());
        assertThat(savedP2.getLabel()).isEqualTo(p2.label());
        assertThat(savedP2.getOpeningDate()).isEqualTo(p2.openingDate());
        assertThat(savedP2.getClosingDate()).isEqualTo(p2.closingDate());
        assertThat(savedP2.getReturnDate()).isEqualTo(p2.returnDate());
        assertThat(savedP2.getOpeningLetterDate()).isEqualTo(p2.openingLetterDate());
        assertThat(savedP2.getOpeningMailDate()).isEqualTo(p2.openingMailDate());
        assertThat(savedP2.getFollowupLetter1Date()).isEqualTo(p2.followupLetter1Date());
        assertThat(savedP2.getFollowupLetter2Date()).isEqualTo(p2.followupLetter2Date());
        assertThat(savedP2.getFollowupLetter3Date()).isEqualTo(p2.followupLetter3Date());
        assertThat(savedP2.getFollowupLetter4Date()).isEqualTo(p2.followupLetter4Date());
        assertThat(savedP2.getFollowupMail1Date()).isEqualTo(p2.followupMail1Date());
        assertThat(savedP2.getFollowupMail2Date()).isEqualTo(p2.followupMail2Date());
        assertThat(savedP2.getFollowupMail3Date()).isEqualTo(p2.followupMail3Date());
        assertThat(savedP2.getFollowupMail4Date()).isEqualTo(p2.followupMail4Date());
        assertThat(savedP2.getFormalNoticeDate()).isEqualTo(p2.formalNoticeDate());
        assertThat(savedP2.getNoReplyDate()).isEqualTo(p2.noReplyDate());
        assertThat(savedP2.getCampaign()).isSameAs(savedCampaign);
    }

}
