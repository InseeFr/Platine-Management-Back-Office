package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;

import java.util.List;

public interface SourceService {

    Source findById(String source);

    List<Source> findAll();

    Source insertOrUpdateSource(Source source);

    void deleteSourceById(String id);

}
