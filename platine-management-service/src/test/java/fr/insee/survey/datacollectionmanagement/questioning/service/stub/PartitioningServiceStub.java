package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class PartitioningServiceStub implements PartitioningService {

    public List<Partitioning> savedPartitionings = new ArrayList<>();

    @Getter
    private Partitioning lastSaved;

    @Override
    public Partitioning getById(String id) {
        return lastSaved;
    }

    @Override
    public Optional<Partitioning> findById(String id) {
        return Optional.of(getById(id));
    }

    @Override
    public Partitioning insertOrUpdatePartitioning(Partitioning partitioning) {
        savedPartitionings.removeIf(x -> Objects.equals(x.getId(), partitioning.getId()));
        savedPartitionings.add(partitioning);
        lastSaved = partitioning;
        return partitioning;
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

    public void setSavedPartitionings(List<Partitioning> partitionings) {
        this.savedPartitionings = new ArrayList<>(partitionings);
    }
}
