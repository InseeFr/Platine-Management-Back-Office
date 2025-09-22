package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.strategy;

import org.springframework.stereotype.Component;

@Component
public class RetrieveSourceFromPartitionStrategy implements SourceRetrievalStrategy {
    /**
     * Retrieve source from interrogation
     * @param partitionId
     * @return
     */
    @Override
    public String getSourceId(Object partitionId) {
        return "1";
    }
}
