package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

public class SourceRetrievalStrategyException extends RuntimeException {
    public SourceRetrievalStrategyException(Object targetedObject) {
        super(String.format("Missing source retrieval strategies for given object %s", targetedObject));
    }
}
