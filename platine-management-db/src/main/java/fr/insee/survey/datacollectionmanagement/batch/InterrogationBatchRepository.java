package fr.insee.survey.datacollectionmanagement.batch;

import fr.insee.survey.datacollectionmanagement.batch.model.Interrogation;

public interface InterrogationBatchRepository {
    void upsert(Interrogation interrogation);
}
