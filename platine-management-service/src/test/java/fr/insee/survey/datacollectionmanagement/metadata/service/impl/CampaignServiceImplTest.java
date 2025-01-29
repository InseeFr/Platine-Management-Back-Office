package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.PartitioningServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CampaignServiceImplTest {

//    Stub
    private CampaignRepositoryStub campaignRepositoryStub;
    private PartitioningServiceStub partitioningServiceStub;
//    Mockito
//    private CampaignRepository campaignRepositoryMock;
//    private PartitioningService partitioningServiceMock;

    private CampaignServiceImpl campaignServiceImpl;

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

//    Stub
        campaignRepositoryStub = new CampaignRepositoryStub();
        campaignRepositoryStub.setCampaigns(campaigns);
        campaignServiceImpl = new CampaignServiceImpl(campaignRepositoryStub, partitioningServiceStub);

//    Mockito
//       partitioningServiceMock = Mockito.mock(PartitioningService.class);
//       campaignRepositoryMock = Mockito.mock(CampaignRepository.class);
//       when(campaignRepositoryMock.findAll()).thenReturn(campaigns);
//       campaignServiceImpl = new CampaignServiceImpl(campaignRepositoryMock, partitioningServiceMock);


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
    void findbyPeriod() {

    }

    @Test
    void findById() {

    }

    @Test
    void findbySourceYearPeriod() {
    }

    @Test
    void findbySourcePeriod() {
    }

    @Test
    void findAll() {
    }

    @Test
    void insertOrUpdateCampaign() {
    }

    @Test
    void deleteCampaignById() {
    }

    @Test
    void addPartitionigToCampaign() {
    }

    @Test
    void isCampaignOngoing() {
    }
}