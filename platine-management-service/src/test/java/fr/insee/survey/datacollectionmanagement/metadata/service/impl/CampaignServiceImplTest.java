package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.ParametersServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.PartitioningServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
        campaignServiceImpl = new CampaignServiceImpl(campaignRepositoryStub, partitioningServiceStub, parametersServiceStub, modelMapper);

    }

    @Test
    void getCampaigns() {
        // given
        List<CampaignMoogDto> moogCampaigns = new ArrayList<>();

        Collection<CampaignMoogDto> result = campaignServiceImpl.getCampaigns();

        // when and then
        assertThat(result).isNotNull().map(CampaignMoogDto::getId).containsExactlyInAnyOrder("c1", "c2", "c3");
        assertThat(result).hasSize(3);
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