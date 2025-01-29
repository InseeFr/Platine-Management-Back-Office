package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;

import java.util.Date;

public class PartitioningServiceStub implements PartitioningService {
    @Override
    public Partitioning findById(String id) {
        return null;
    }

    @Override
    public Partitioning insertOrUpdatePartitioning(Partitioning partitioning) {
        return null;
    }

    @Override
    public void deletePartitioningById(String id) {

    }

    @Override
    public boolean isOnGoing(Partitioning part, Date date) {
        return false;
    }

}
