package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.strategy;

import org.springframework.stereotype.Component;

@Component
public class RetrieveSourceFromInterrogationStrategy implements SourceRetrievalStrategy {
    /**
     * Retrieve source from interrogation
     * @return
     */
    @Override
    public String getSourceId(Object interrogationId) {
        // treatments here to retrieve the source id
        return "1";
    }
}
