package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignSummaryDto;
import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.CampaignRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.ParametersServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.PartitioningServiceStub;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class CampaignServiceImplTest {

    private CampaignRepositoryStub campaignRepositoryStub;
    private PartitioningServiceStub partitioningServiceStub;
    private ParametersServiceStub parametersServiceStub;
    @Autowired
    ModelMapper modelMapper;
    private CampaignServiceImpl campaignServiceImpl;

    private Campaign campaign;

    @BeforeEach
    void init() {
        Campaign c1 = new Campaign();
        c1.setId("c1");
        Partitioning partitioning1 = new Partitioning();
        partitioning1.setId("partitioning1");
        partitioning1.setOpeningDate(new Date());
        partitioning1.setClosingDate(new Date());
        Set<Partitioning> partitionings1 = Set.of(partitioning1);
        c1.setPartitionings(partitionings1);

        Campaign c2 = new Campaign();
        c2.setId("c2");
        Partitioning partitioning2 = new Partitioning();
        partitioning2.setId("partitioning1");
        partitioning2.setOpeningDate(new Date());
        partitioning2.setClosingDate(new Date());
        Set<Partitioning> partitionings2 = Set.of(partitioning2);
        c2.setPartitionings(partitionings2);

        Campaign c3 = new Campaign();
        c3.setId("c3");
        Partitioning partitioning3 = new Partitioning();
        partitioning3.setId("partitioning1");
        partitioning3.setOpeningDate(new Date());
        partitioning3.setClosingDate(new Date());
        Set<Partitioning> partitionings3 = Set.of(partitioning3);
        c3.setPartitionings(partitionings3);

        List<Campaign> campaigns = List.of(c1,c2,c3);

        campaign = new Campaign();
        campaign.setId("testCampaign");

        campaignRepositoryStub = new CampaignRepositoryStub();
        campaignRepositoryStub.setCampaigns(campaigns);
        parametersServiceStub = new ParametersServiceStub();
        partitioningServiceStub = new PartitioningServiceStub();
        campaignServiceImpl = new CampaignServiceImpl(campaignRepositoryStub, partitioningServiceStub, parametersServiceStub, modelMapper);
    }

    @Test
    void getCampaigns() {
        // given
        Partitioning partitioning1 = createPartitioning("c1", 0L, 0L);
        Partitioning partitioning2 = createPartitioning("c2", 0L, 0L);
        Partitioning partitioning3 = createPartitioning("c3", 0L, 0L);
        Campaign c1 = createCampaign("AAA", "c1", 2021, PeriodEnum.M01, Set.of(partitioning1));
        Campaign c2 = createCampaign("BBB", "c2", 2021, PeriodEnum.M01, Set.of(partitioning2));
        Campaign c3 = createCampaign("CCC", "c3", 2021, PeriodEnum.M01, Set.of(partitioning3));
        campaignRepositoryStub.setCampaigns(List.of(c1, c2, c3));

        // When
        Collection<CampaignMoogDto> result = campaignServiceImpl.getCampaigns();

        // Then
        assertThat(result).isNotNull().map(CampaignMoogDto::getId).containsExactlyInAnyOrder("c1", "c2", "c3");
        assertThat(result).hasSize(3);
    }

    private Campaign createCampaign(String sourceId, String id, int year, PeriodEnum periodEnum, Set<Partitioning> partitioningSet) {
        Campaign c = new Campaign();
        c.setId(id);
        c.setYear(year);
        c.setPeriod(periodEnum);
        Source source = new Source();
        source.setId(sourceId);
        Survey survey = new Survey();
        survey.setYear(year);
        survey.setSource(source);
        c.setSurvey(survey);
        c.setPartitionings(partitioningSet);
        return c;
    }

    private Partitioning createPartitioning(String idCampaign, long daysBefore, long daysAfter) {
        Partitioning partitioning = new Partitioning();
        Instant instant = Instant.now();
        partitioning.setOpeningDate(Date.from(instant.minus(daysBefore, ChronoUnit.DAYS)));
        partitioning.setClosingDate(Date.from(instant.plus(daysAfter, ChronoUnit.DAYS)));
        campaign = new Campaign();
        campaign.setId(idCampaign);
        partitioning.setCampaign(campaign);
        return partitioning;
    }

    @Test
    @DisplayName("When search campaigns Returns the first openingDate and the last closingDate between all partitioning of the campaign")
    void searchCampaignsTestDates() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Partitioning partitioning1 = createPartitioning("c1", 1L, 3L);
        Partitioning partitioning2 = createPartitioning("c1", 2L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2);
        Campaign c = createCampaign("AAA","c1", 2021, PeriodEnum.M01, partitioningSet);
        campaignRepositoryStub.setCampaigns(List.of(c));

        // When
        Page<CampaignSummaryDto> result = campaignServiceImpl.searchCampaigns(null, pageRequest);

        // Then
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst().getOpeningDate()).isEqualTo(partitioning2.getOpeningDate());
        assertThat(result.getContent().getFirst().getClosingDate()).isEqualTo(partitioning1.getClosingDate());
    }

    @Test
    @DisplayName("When search campaigns returns campaigns with opended false if no partitioning is ongoing")
    void searchCampaignTestOpenedFalse() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Partitioning partitioning1 = createPartitioning("c1", -3L, -2L);
        Partitioning partitioning2 = createPartitioning("c1", -1L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2);
        Campaign c = createCampaign("AAA", "c1", 2022, PeriodEnum.M01, partitioningSet);
        campaignRepositoryStub.setCampaigns(List.of(c));

        // When
        Page<CampaignSummaryDto> result = campaignServiceImpl.searchCampaigns(null, pageRequest);

        // Then
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(CollectionStatus.CLOSED);
    }

    @Test
    @DisplayName("When search campaigns returns campaigns with opended true if at least one partitioning is ongoing")
    void searchCampaignTestOpenedTrue() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Partitioning partitioning1 = createPartitioning("c1", 3L, -2L);
        Partitioning partitioning2 = createPartitioning("c1", 1L, 1L);
        Partitioning partitioning3 = createPartitioning("c1", -1L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2, partitioning3);
        Campaign c = createCampaign("AAA", "c1", 2023, PeriodEnum.M01, partitioningSet);
        campaignRepositoryStub.setCampaigns(List.of(c));

        // When
        Page<CampaignSummaryDto> result = campaignServiceImpl.searchCampaigns(null, pageRequest);

        // Then
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(CollectionStatus.OPEN);
    }

    @Test
    @DisplayName("When search campaigns returns all campaigns with pagination when searchParam is null")
    void searchCampaignsTestPagination() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2);
        Partitioning partitioning1 = createPartitioning("c1", 1L, 3L);
        Partitioning partitioning2 = createPartitioning("c2",2L, 1L);
        Partitioning partitioning3 = createPartitioning("c3", 3L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2, partitioning3);
        Campaign c1 = createCampaign("AAA", "c1", 2021, PeriodEnum.M01, partitioningSet);
        Campaign c2 = createCampaign("BBB", "c2", 2022, PeriodEnum.M01, partitioningSet);
        Campaign c3 = createCampaign("CCC", "c3", 2023, PeriodEnum.M01, partitioningSet);
        Campaign c4 = createCampaign("CCC", "c4", 2024, PeriodEnum.M01, partitioningSet);
        campaignRepositoryStub.setCampaigns(List.of(c1, c2, c3, c4));

        // When
        Page<CampaignSummaryDto> result = campaignServiceImpl.searchCampaigns(null, pageRequest);

        // Then
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.getContent().get(0).getCampaignId()).isEqualTo("c1");
        assertThat(result.getContent().get(1).getCampaignId()).isEqualTo("c2");
    }

    @Test
    @DisplayName("When search campaigns returns campaign filtered by searchParam")
    void searchCampaignsTestSearchParam() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Partitioning partitioning1 = createPartitioning("c1", 1L, 3L);
        Partitioning partitioning2 = createPartitioning("c2",2L, 1L);
        Partitioning partitioning3 = createPartitioning("c3", 3L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2, partitioning3);
        Campaign c1 = createCampaign("AAA", "c1", 2021, PeriodEnum.M01, partitioningSet);
        Campaign c2 = createCampaign("BBB", "c2", 2022, PeriodEnum.M01, partitioningSet);
        Campaign c3 = createCampaign("BBB", "c3", 2023, PeriodEnum.M01, partitioningSet);
        campaignRepositoryStub.setCampaigns(List.of(c1, c2, c3));

        // When
        Page<CampaignSummaryDto> result = campaignServiceImpl.searchCampaigns("BBB", pageRequest);

        // Then
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.getContent().getFirst().getCampaignId()).isEqualTo("c2");
        assertThat(result.getContent().get(1).getCampaignId()).isEqualTo("c3");
    }

    @Test
    @DisplayName("When search campaigns returns campaign without dates and with undefined status when no partitioning")
    void searchCampaignsTestNoPartitioning() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Campaign c1 = createCampaign("AAA", "c1", 2021, PeriodEnum.M01, new HashSet<>()); // empty partitioning
        Campaign c2 = createCampaign("AAA", "c2", 2022, PeriodEnum.M01, null); // null partitioning
        campaignRepositoryStub.setCampaigns(List.of(c1,c2));

        // When
        Page<CampaignSummaryDto> result = campaignServiceImpl.searchCampaigns(null, pageRequest);

        // Then
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.getContent().getFirst().getOpeningDate()).isNull();
        assertThat(result.getContent().getFirst().getClosingDate()).isNull();
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(CollectionStatus.UNDEFINED);
        assertThat(result.getContent().get(1).getOpeningDate()).isNull();
        assertThat(result.getContent().get(1).getClosingDate()).isNull();
        assertThat(result.getContent().get(1).getStatus()).isEqualTo(CollectionStatus.UNDEFINED);
    }


    @Test
    @DisplayName("When search campaigns return campaignSummaryDto")
    void searchCampaignsTestCampaignSummaryDto() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Partitioning partitioning1 = createPartitioning("c1", 1L, 3L);
        Partitioning partitioning2 = createPartitioning("c1",2L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2);
        Campaign c = createCampaign("AAA","c1", 2021, PeriodEnum.X08, partitioningSet);
        campaignRepositoryStub.setCampaigns(List.of(c));

        // When
        Page<CampaignSummaryDto> result = campaignServiceImpl.searchCampaigns(null, pageRequest);

        // Then
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getContent().getFirst().getCampaignId()).isEqualTo("c1");
        assertThat(result.getContent().getFirst().getSource()).isEqualTo("AAA");
        assertThat(result.getContent().getFirst().getYear()).isEqualTo(2021);
        assertThat(result.getContent().getFirst().getPeriod()).isEqualTo("pluriannuel X08");
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(CollectionStatus.OPEN);
        assertThat(result.getContent().getFirst().getOpeningDate()).isEqualTo(partitioning2.getOpeningDate());
        assertThat(result.getContent().getFirst().getClosingDate()).isEqualTo(partitioning1.getClosingDate());
    }

    @Test
    @DisplayName("When search campaigns return no campaignSummaryDto if no campaign")
    void searchCampaignsTestNoCampaign() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        campaignRepositoryStub.setCampaigns(new ArrayList<>());

        // When
        Page<CampaignSummaryDto> result = campaignServiceImpl.searchCampaigns(null, pageRequest);

        // Then
        assertThat(result).isNotNull().isEmpty();
    }


    @Test
    void testIsCampaignInType_WithEmptyCampaignType() {
        boolean result = campaignServiceImpl.isCampaignInType(campaign, "");
        assertThat(result).isTrue();
    }

    @Test
    void testIsCampaignInType_WithNullCampaignType() {
        boolean result = campaignServiceImpl.isCampaignInType(campaign, null);
        assertThat(result).isTrue();
    }


    @Test
    void testIsCampaignInType_WithMatchingV3AndNonEmptyUrlType() {
        parametersServiceStub.setParameterValue(campaign, ParameterEnum.URL_TYPE, "V3");

        boolean result = campaignServiceImpl.isCampaignInType(campaign, "V3");
        assertThat(result).isTrue();
    }

    @Test
    void testIsCampaignInType_WithV3ButEmptyUrlType() {
        parametersServiceStub.setParameterValue(campaign, ParameterEnum.URL_TYPE, "");

        boolean result = campaignServiceImpl.isCampaignInType(campaign, "V3");
        assertThat(result).isTrue();
    }

    @Test
    void testIsCampaignInType_WithNonMatchingCampaignType() {
        parametersServiceStub.setParameterValue(campaign, ParameterEnum.URL_TYPE, "V1");

        boolean result = campaignServiceImpl.isCampaignInType(campaign, "V3");
        assertThat(result).isFalse();
    }

    @Test
    void testIsCampaignInType_WithMatchingNonV3CampaignType() {
        parametersServiceStub.setParameterValue(campaign, ParameterEnum.URL_TYPE, "V1");

        boolean result = campaignServiceImpl.isCampaignInType(campaign, "V1");
        assertThat(result).isTrue();
    }


    @Test
    void testIsCampaignOngoingFalse_WhenCampaignHasNoPartitioning(){
        Campaign camp = new Campaign();
        assertThat(campaignServiceImpl.isCampaignOngoing(camp)).isFalse();

    }
}