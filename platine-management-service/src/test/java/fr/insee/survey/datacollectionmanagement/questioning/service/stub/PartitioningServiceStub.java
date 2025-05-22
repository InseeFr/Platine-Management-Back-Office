package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PartitioningServiceStub implements PartitioningService {

    private Partitioning stubbedPartition;

    @Override
    public Partitioning findById(String id) {
        return stubbedPartition;
    }

    @Override
    public Partitioning insertOrUpdatePartitioning(Partitioning partitioning) {
        stubbedPartition = partitioning;
        return stubbedPartition;
    }

    @Override
    public void deletePartitioningById(String id) {
        //not used
    }

    @Override
    public boolean isOnGoing(Partitioning part, Instant now) {
        return part.getClosingDate().toInstant().isAfter(now) &&
                part.getOpeningDate().toInstant().isBefore(now);
    }

}
