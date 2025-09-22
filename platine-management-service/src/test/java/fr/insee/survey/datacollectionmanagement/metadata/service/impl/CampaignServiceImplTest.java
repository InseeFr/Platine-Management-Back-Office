package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import com.github.f4b6a3.uuid.UuidCreator;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.*;
import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.CampaignRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.ParametersServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.PartitioningServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignServiceImplTest {

    private CampaignRepositoryStub campaignRepositoryStub;
    private final PartitioningServiceStub partitioningServiceStub = new PartitioningServiceStub();
    private final ParametersServiceStub parametersServiceStub = new ParametersServiceStub();
    private final ModelMapper modelMapper = new ModelMapper();
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

        List<Campaign> campaigns = List.of(c1, c2, c3);

        campaign = new Campaign();
        campaign.setId("testCampaign");

        campaignRepositoryStub = new CampaignRepositoryStub();
        campaignRepositoryStub.setCampaigns(campaigns);
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
        return createCampaign(sourceId, id, year, periodEnum, partitioningSet, "other campaign wording");
    }

    private Campaign createCampaign(String sourceId, String id, int year, PeriodEnum periodEnum, Set<Partitioning> partitioningSet, String campaignWording) {
        Campaign c = new Campaign();
        c.setId(id);
        c.setYear(year);
        c.setPeriod(periodEnum);
        c.setCampaignWording(campaignWording);
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
        Campaign c = createCampaign("AAA", "c1", 2021, PeriodEnum.M01, partitioningSet);
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
        Partitioning partitioning2 = createPartitioning("c2", 2L, 1L);
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
        Partitioning partitioning2 = createPartitioning("c2", 2L, 1L);
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
        campaignRepositoryStub.setCampaigns(List.of(c1, c2));

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
        Partitioning partitioning2 = createPartitioning("c1", 2L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2);
        Campaign c = createCampaign("AAA", "c1", 2021, PeriodEnum.X08, partitioningSet);
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
    void testIsCampaignOngoingFalse_WhenCampaignHasNoPartitioning() {
        Campaign camp = new Campaign();
        camp.setId("MMM2025X00");
        campaignRepositoryStub.setCampaigns(List.of(camp));

        assertThat(campaignServiceImpl.isCampaignOngoing("MMM2025X00")).isFalse();

    }

    @Test
    @DisplayName("Get Campaign Header with status CLOSED if no partitioning is ongoing")
    void getCampaignHeaderTestOpenedFalse() {
        // given
        Partitioning partitioning1 = createPartitioning("c1", -3L, -2L);
        Partitioning partitioning2 = createPartitioning("c1", -1L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2);
        Campaign c = createCampaign("AAA", "c1", 2022, PeriodEnum.M01, partitioningSet);
        campaignRepositoryStub.setCampaigns(List.of(c));

        // When
        CampaignHeaderDto result = campaignServiceImpl.findCampaignHeaderById("c1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(CollectionStatus.CLOSED);
    }

    @Test
    @DisplayName("Get Campaign Header with status OPEN if at least one partitioning is ongoing")
    void getCampaignHeaderTestOpenedTrue() {
        // given
        Partitioning partitioning1 = createPartitioning("c1", 3L, -2L);
        Partitioning partitioning2 = createPartitioning("c1", 1L, 1L);
        Partitioning partitioning3 = createPartitioning("c1", -1L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2, partitioning3);
        Campaign c = createCampaign("AAA", "c1", 2023, PeriodEnum.M01, partitioningSet);
        campaignRepositoryStub.setCampaigns(List.of(c));

        // When
        CampaignHeaderDto result = campaignServiceImpl.findCampaignHeaderById("c1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(CollectionStatus.OPEN);
    }

    @Test
    @DisplayName("Get Campaign Header with status UNDEFINED when no partitioning")
    void getCampaignHeaderTestNoPartitioning() {
        // given
        Campaign c1 = createCampaign("AAA", "c1", 2021, PeriodEnum.M01, new HashSet<>()); // empty partitioning
        Campaign c2 = createCampaign("AAA", "c2", 2022, PeriodEnum.M01, null); // null partitioning
        campaignRepositoryStub.setCampaigns(List.of(c1, c2));

        // When
        CampaignHeaderDto result1 = campaignServiceImpl.findCampaignHeaderById("c1");
        CampaignHeaderDto result2 = campaignServiceImpl.findCampaignHeaderById("c2");

        // Then
        assertThat(result1).isNotNull();
        assertThat(result1.getStatus()).isEqualTo(CollectionStatus.UNDEFINED);
        assertThat(result2).isNotNull();
        assertThat(result2.getStatus()).isEqualTo(CollectionStatus.UNDEFINED);

    }

    @Test
    @DisplayName("get Campaign Header return CampaignHeaderDto")
    void getCampaignHeaderTest() {
        // given
        Partitioning partitioning1 = createPartitioning("c1", 1L, 3L);
        Partitioning partitioning2 = createPartitioning("c1", 2L, 1L);
        Set<Partitioning> partitioningSet = Set.of(partitioning1, partitioning2);
        Campaign c = createCampaign("AAA", "c1", 2021, PeriodEnum.X08, partitioningSet, "campaign wording");
        campaignRepositoryStub.setCampaigns(List.of(c));

        // When
        CampaignHeaderDto result = campaignServiceImpl.findCampaignHeaderById("c1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCampaignId()).isEqualTo("c1");
        assertThat(result.getSource()).isEqualTo("AAA");
        assertThat(result.getYear()).isEqualTo(2021);
        assertThat(result.getPeriod()).isEqualTo("pluriannuel X08");
        assertThat(result.getStatus()).isEqualTo(CollectionStatus.OPEN);
        assertThat(result.getWording()).isEqualTo("campaign wording");
    }

    @Test
    void findCampaignStatusByCampaignIdIn_shouldReturnCorrectStatus() {
        // Given
        Campaign campaign1 = new Campaign();
        campaign1.setId("CAMP1");
        Partitioning partitioning1 = new Partitioning();
        partitioning1.setOpeningDate(new Date(System.currentTimeMillis() - 86400000));
        partitioning1.setClosingDate(new Date(System.currentTimeMillis() + 86400000));
        campaign1.setPartitionings(Set.of(partitioning1));

        Campaign campaign2 = new Campaign();
        campaign2.setId("CAMP2");
        Partitioning partitioning2 = new Partitioning();
        partitioning2.setOpeningDate(new Date(System.currentTimeMillis() - 86400000 * 10));
        partitioning2.setClosingDate(new Date(System.currentTimeMillis() - 86400000 * 5));
        campaign2.setPartitionings(Set.of(partitioning2));

        Campaign campaign3 = new Campaign();
        campaign2.setId("CAMP3");

        campaignRepositoryStub.setCampaigns(List.of(campaign1, campaign2, campaign3));

        // When
        List<CampaignStatusDto> statuses = campaignServiceImpl.findCampaignStatusByCampaignIdIn(List.of("CAMP1", "CAMP2", "CAMP3"));

        // Then
        assertThat(statuses).isNotNull()
            .hasSize(3);
        assertThat(statuses).extracting(CampaignStatusDto::id).containsExactlyInAnyOrder("CAMP1", "CAMP2", "CAMP3");
        assertThat(statuses).extracting(CampaignStatusDto::status)
                .containsExactlyInAnyOrder(CollectionStatus.OPEN, CollectionStatus.CLOSED, CollectionStatus.UNDEFINED);
    }

    @Test
    void findCampaignStatusByCampaignIdIn_shouldHandleNullCampaignId() {
        List<CampaignStatusDto> status = campaignServiceImpl.findCampaignStatusByCampaignIdIn(Collections.singletonList(null));
        assertThat(status).isEmpty();
    }

    @Test
    void getCampaignCommonsOngoingDtos_should_return_only_running_campaigns() {
        Campaign camp1 = openedCampaign("CAMP1");
        Campaign camp2 = fileUploadCampaign("CAMP2");
        Campaign camp3 = futureCampaign("CAMP3");

        campaignRepositoryStub.setCampaigns(List.of(camp1, camp2, camp3));

        List<CampaignCommonsDto> result = campaignServiceImpl.getCampaignCommonsOngoingDtos();

        assertThat(result).isNotNull()
                .hasSize(1);
        assertThat(result.getFirst()).isNotNull();
        assertThat(result.getFirst().dataCollectionTarget()).isEqualTo("LUNATIC_NORMAL");
        assertThat(result.getFirst().id()).isEqualTo("CAMP1");
        assertThat(result.getFirst().collectMode()).isEqualTo("WEB");
        assertThat(result.getFirst().sensitivity()).isFalse();


    }

    @Test
    void insertOrUpdateCampaign_shouldCreateWithGeneratedTechnicalId_andDefaultTarget() {
        // given
        Campaign newCamp = new Campaign();
        newCamp.setId("NEW1");
        newCamp.setDataCollectionTarget(null);

        // when
        Campaign saved = campaignServiceImpl.insertOrUpdateCampaign(newCamp);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getTechnicalId()).isNotNull();
        assertThat(saved.getDataCollectionTarget())
                .isEqualTo(DataCollectionEnum.LUNATIC_NORMAL);
    }

    @Test
    void insertOrUpdateCampaign_shouldKeepExistingTechnicalId_onUpdate() {
        // given
        Campaign existing = new Campaign();
        existing.setId("EXIST1");
        existing.setTechnicalId(UuidCreator.getTimeOrderedEpoch());
        existing.setDataCollectionTarget(DataCollectionEnum.FILE_UPLOAD);
        campaignRepositoryStub.setCampaigns(List.of(existing));

        Campaign toUpdate = new Campaign();
        toUpdate.setId("EXIST1");
        toUpdate.setDataCollectionTarget(DataCollectionEnum.LUNATIC_SENSITIVE);

        // when
        Campaign result = campaignServiceImpl.insertOrUpdateCampaign(toUpdate);

        // then
        assertThat(result.getTechnicalId())
                .isEqualTo(existing.getTechnicalId());

        assertThat(result.getDataCollectionTarget())
                .isEqualTo(DataCollectionEnum.LUNATIC_SENSITIVE);
    }

    @Test
    void insertOrUpdateCampaign_shouldNotOverrideDataCollectionTarget_whenAlreadySetOnCreate() {
        // given
        Campaign newCamp = new Campaign();
        newCamp.setId("CAMP_WITH_TARGET");
        newCamp.setDataCollectionTarget(DataCollectionEnum.FILE_UPLOAD);

        // when
        Campaign saved = campaignServiceImpl.insertOrUpdateCampaign(newCamp);

        // then
        assertThat(saved.getTechnicalId()).isNotNull();
        assertThat(saved.getDataCollectionTarget()).isEqualTo(DataCollectionEnum.FILE_UPLOAD);
    }

    private Campaign openedCampaign(String id) {
        Campaign c = new Campaign();
        c.setId(id);
        c.setPartitionings(Set.of(partitioning(true)));
        c.setDataCollectionTarget(DataCollectionEnum.LUNATIC_NORMAL);
        c.setSensitivity(false);
        return c;
    }

    private Campaign fileUploadCampaign(String id) {
        Campaign c = new Campaign();
        c.setId(id);
        c.setPartitionings(Set.of(partitioning(true)));
        c.setDataCollectionTarget(DataCollectionEnum.FILE_UPLOAD);
        c.setSensitivity(false);
        return c;
    }

    private Campaign futureCampaign(String id) {
        Campaign c = new Campaign();
        c.setId(id);
        c.setPartitionings(Set.of(partitioning(false)));
        c.setDataCollectionTarget(DataCollectionEnum.LUNATIC_SENSITIVE);
        c.setSensitivity(true);
        return c;
    }

    private Partitioning partitioning(boolean opened) {
        Partitioning p = new Partitioning();
        p.setId(UUID.randomUUID().toString());
        if (opened) {
            p.setOpeningDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
            p.setClosingDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        } else {
            p.setOpeningDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            p.setClosingDate(Date.from(Instant.now().plus(2, ChronoUnit.DAYS)));
        }
        return p;
    }
}