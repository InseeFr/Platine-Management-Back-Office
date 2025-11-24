package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;

import java.time.Instant;
import java.util.Optional;

public interface PartitioningService {

    Partitioning getById(String id);

    Optional<Partitioning> findById(String id);

    Partitioning insertOrUpdatePartitioning(Partitioning partitioning);

    void deletePartitioningById(String id);

    boolean isOnGoing(Partitioning part, Instant instant);
}
