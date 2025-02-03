package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub.PartitioningRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class PartitioningServiceImplTest {

    PartitioningRepositoryStub partitioningRepositoryStub;
    PartitioningService partitioningService;

    @BeforeEach
    void init() {
        partitioningRepositoryStub = new PartitioningRepositoryStub();
        partitioningService = new PartioningServiceImpl(partitioningRepositoryStub);
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
        assertThat(partitioningService.isOnGoing(part, new Date())).isTrue();
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

        assertThat(partitioningService.isOnGoing(part, new Date())).isFalse();
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

        assertThat(partitioningService.isOnGoing(part, new Date())).isFalse();
    }
}