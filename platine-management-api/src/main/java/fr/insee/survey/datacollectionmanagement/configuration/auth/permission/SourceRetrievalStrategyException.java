package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

public class SourceRetrievalStrategyException extends RuntimeException {
    public SourceRetrievalStrategyException(String errorMessage) {
        super(errorMessage);
    }
}
