package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartitioningServiceImpl implements PartitioningService {

    private final PartitioningRepository partitioningRepository;

    @Override
    public Partitioning findById(String id) {
        return partitioningRepository.findById(id).
                orElseThrow(() -> new NotFoundException(String.format("Partitioning %s not found", id)));

    }

    @Override
    public Partitioning insertOrUpdatePartitioning(Partitioning partitioning) {
        return partitioningRepository.save(partitioning);

    }

    @Override
    public void deletePartitioningById(String id) {
        partitioningRepository.deleteById(id);
    }

    @Override
    public boolean isOnGoing(Partitioning part, Instant now) {
        return part.getClosingDate().toInstant().isAfter(now) &&
                part.getOpeningDate().toInstant().isBefore(now);
    }
}
