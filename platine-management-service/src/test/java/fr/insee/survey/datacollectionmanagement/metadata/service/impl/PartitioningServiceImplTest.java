package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.PartitioningRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

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
        part.setOpeningDate(Instant.now().minus(1, ChronoUnit.DAYS));
        part.setClosingDate(Instant.now().plus(1, ChronoUnit.DAYS));
        assertThat(partitioningService.isOnGoing(part, Instant.now())).isTrue();
    }

    @Test
    void isOnGoingFalse_WhenPartitioningOpensTomorrowAndCloseAnytime() {
        int nbDays = new Random().nextInt(1000);
        Partitioning part = new Partitioning();
        part.setOpeningDate(Instant.now().plus(1, ChronoUnit.DAYS));
        part.setClosingDate(Instant.now().plus(1 + nbDays, ChronoUnit.DAYS));
        assertThat(partitioningService.isOnGoing(part, Instant.now())).isFalse();
    }

    @Test
    void isOnGoingFalse_WhenPartitioningOpensAnytimeAndCloseYesterday() {
        int nbDays = new Random().nextInt(1000);
        Partitioning part = new Partitioning();
        part.setOpeningDate(Instant.now().minus(nbDays, ChronoUnit.DAYS));
        part.setClosingDate(Instant.now().minus(1, ChronoUnit.DAYS));
        assertThat(partitioningService.isOnGoing(part, Instant.now())).isFalse();
    }
}
