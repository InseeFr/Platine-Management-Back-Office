package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.strategy;

import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import org.springframework.stereotype.Component;

@Component
public class RetrieveSourceFromPartitionStrategy implements SourceRetrievalStrategy {

    PartitioningService partitioningService;

    /**
     * Retrieve source from partition id
     * @param partitionId partitionId
     * @return sourceId linked to partition
     */
    @Override
    public String getSourceId(Object partitionId) {
        return partitioningService.getById((String) partitionId).getCampaign().getSurvey().getSource().getId();
    }
}
