package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;

import java.util.Date;

public interface PartitioningService {

    Partitioning findById(String id);

    Partitioning insertOrUpdatePartitioning(Partitioning partitioning);

    void deletePartitioningById(String id);

    boolean isOnGoing(Partitioning part, Date date);


}
