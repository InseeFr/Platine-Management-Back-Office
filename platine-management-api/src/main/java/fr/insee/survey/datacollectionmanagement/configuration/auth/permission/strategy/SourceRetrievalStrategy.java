package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.strategy;

public interface SourceRetrievalStrategy {
    String getSourceId(Object targetDomainObject);
}