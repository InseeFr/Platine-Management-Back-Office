package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.dto.input.ContextCreateDto;

public interface ContextService {
    void saveContext(ContextCreateDto context);
}
