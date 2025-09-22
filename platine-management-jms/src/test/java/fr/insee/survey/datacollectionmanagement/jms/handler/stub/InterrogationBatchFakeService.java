package fr.insee.survey.datacollectionmanagement.jms.handler.stub;


import fr.insee.survey.datacollectionmanagement.batch.model.Interrogation;
import fr.insee.survey.datacollectionmanagement.metadata.service.InterrogationBatchException;
import fr.insee.survey.datacollectionmanagement.metadata.service.InterrogationBatchService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class InterrogationBatchFakeService implements InterrogationBatchService {

    public static final String RUNTIME_EXCEPTION_MESSAGE = "runtime exception";

    public static final String INTERROGATION_BATCH_EXCEPTION = "InterrogationBatchException exception";

    @Getter
    private Interrogation interrogationBatchUsed = null;

    @Setter
    private boolean shouldThrowInterrogationBatchException = false;

    @Setter
    private boolean shouldThrowRuntimeException = false;


    @Override
    public void saveInterrogations(List<Interrogation> interrogations) throws InterrogationBatchException {
        // not used at this moment
    }

    @Override
    public void saveInterrogation(Interrogation interrogation) throws InterrogationBatchException {
        if(shouldThrowInterrogationBatchException) {
            throw new InterrogationBatchException(INTERROGATION_BATCH_EXCEPTION);
        }

        if(shouldThrowRuntimeException) {
            throw new RuntimeException(RUNTIME_EXCEPTION_MESSAGE);
        }

        interrogationBatchUsed = interrogation;
    }
}
