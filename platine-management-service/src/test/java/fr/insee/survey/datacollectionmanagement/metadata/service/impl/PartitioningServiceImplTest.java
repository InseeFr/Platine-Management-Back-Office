package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import com.github.f4b6a3.uuid.UuidCreator;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.PartitioningRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class PartitioningServiceImplTest {

    PartitioningRepositoryStub partitioningRepositoryStub;
    PartitioningService partitioningService;

    @BeforeEach
    void init() {
        partitioningRepositoryStub = new PartitioningRepositoryStub();
        partitioningService = new PartitioningServiceImpl(partitioningRepositoryStub);
    }

    @Test
    void isOnGoing_WhenPartitioningOpensYesterdayAndCloseTomorrow() {
        Partitioning part = new Partitioning();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date openingDate = cal.getTime();
        part.setOpeningDate(openingDate);

        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date closingDate = cal.getTime();
        part.setClosingDate(closingDate);
        assertThat(partitioningService.isOnGoing(part, Instant.now())).isTrue();
    }

    @Test
    void isOnGoingFalse_WhenPartitioningOpensTomorrowAndCloseAnytime() {
        Partitioning part = new Partitioning();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date openingDate = cal.getTime();
        part.setOpeningDate(openingDate);

        Random rand = new Random();
        int nbDays = rand.nextInt(1000);
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, nbDays);
        Date closingDate = cal.getTime();
        part.setClosingDate(closingDate);

        assertThat(partitioningService.isOnGoing(part, Instant.now())).isFalse();
    }

    @Test
    void isOnGoingFalse_WhenPartitioningOpensAnytimeAndCloseYesterday() {
        Random rand = new Random();
        int nbDays = rand.nextInt(1000);
        Partitioning part = new Partitioning();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -nbDays);
        Date openingDate = cal.getTime();
        part.setOpeningDate(openingDate);

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date closingDate = cal.getTime();
        part.setClosingDate(closingDate);

        assertThat(partitioningService.isOnGoing(part, Instant.now())).isFalse();
    }

    @Test
    void insertOrUpdatePartitioning_shouldCreateWithGeneratedTechnicalId_whenPartitioningDoesNotExist() {
        // given: a new partitioning not present in the repository
        Partitioning newPart = new Partitioning();
        newPart.setId("P_NEW");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        newPart.setOpeningDate(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 2);
        newPart.setClosingDate(cal.getTime());

        // when: saving for the first time
        Partitioning saved = partitioningService.insertOrUpdatePartitioning(newPart);

        // then: a technicalId must be generated and entity persisted in the stub
        assertThat(saved).isNotNull();
        assertThat(saved.getTechnicalId()).as("technicalId must be generated on create").isNotNull();

        Optional<Partitioning> reloaded = partitioningRepositoryStub.findById("P_NEW");
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().getTechnicalId()).isNotNull();
        assertThat(reloaded.get().getOpeningDate()).isEqualTo(newPart.getOpeningDate());
        assertThat(reloaded.get().getClosingDate()).isEqualTo(newPart.getClosingDate());
    }

    @Test
    void insertOrUpdatePartitioning_shouldKeepExistingTechnicalId_onUpdate() {
        // given
        Partitioning existing = new Partitioning();
        existing.setId("P_EXIST");
        existing.setTechnicalId(UuidCreator.getTimeOrderedEpoch());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        existing.setOpeningDate(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 10);
        existing.setClosingDate(cal.getTime());
        partitioningRepositoryStub.setPartitionings(existing);

        Partitioning toUpdate = new Partitioning();
        toUpdate.setId("P_EXIST");
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_MONTH, -2);
        toUpdate.setOpeningDate(cal2.getTime());
        cal2.add(Calendar.DAY_OF_MONTH, 4);
        toUpdate.setClosingDate(cal2.getTime());

        // when
        Partitioning result = partitioningService.insertOrUpdatePartitioning(toUpdate);

        // then
        assertThat(result.getTechnicalId())
                .as("technicalId must be preserved on update")
                .isEqualTo(existing.getTechnicalId());

        Partitioning persisted = partitioningRepositoryStub.findById("P_EXIST").orElseThrow();
        assertThat(persisted.getTechnicalId()).isEqualTo(existing.getTechnicalId());
        assertThat(persisted.getOpeningDate()).isEqualTo(toUpdate.getOpeningDate());
        assertThat(persisted.getClosingDate()).isEqualTo(toUpdate.getClosingDate());
    }

    @Test
    void insertOrUpdatePartitioning_shouldNotOverrideTechnicalId_ifAlreadySetAndEntityIsNew() {
        // given
        UUID randomUUID = UUID.randomUUID();
        Partitioning newPart = new Partitioning();
        newPart.setId("P_EDGE");
        newPart.setTechnicalId(randomUUID);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        newPart.setOpeningDate(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 3);
        newPart.setClosingDate(cal.getTime());

        // when
        Partitioning saved = partitioningService.insertOrUpdatePartitioning(newPart);

        // then
        assertThat(saved.getTechnicalId())
                .as("technicalId must be generated even if a value was pre-set, since the entity is new")
                .isNotNull()
                .isNotEqualTo(randomUUID);
    }

}