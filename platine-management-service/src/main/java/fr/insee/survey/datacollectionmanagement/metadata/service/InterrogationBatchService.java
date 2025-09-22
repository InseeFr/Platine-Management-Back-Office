package fr.insee.survey.datacollectionmanagement.metadata.service;


import fr.insee.survey.datacollectionmanagement.batch.model.Interrogation;

import java.util.List;

public interface InterrogationBatchService {
    void saveInterrogations(List<Interrogation> interrogations) throws InterrogationBatchException;
    void saveInterrogation(Interrogation interrogations) throws InterrogationBatchException;
}
