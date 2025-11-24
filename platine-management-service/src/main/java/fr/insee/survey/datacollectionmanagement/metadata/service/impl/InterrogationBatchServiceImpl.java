package fr.insee.survey.datacollectionmanagement.metadata.service.impl;


import fr.insee.survey.datacollectionmanagement.batch.InterrogationBatchRepository;
import fr.insee.survey.datacollectionmanagement.batch.model.Interrogation;
import fr.insee.survey.datacollectionmanagement.metadata.service.InterrogationBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class InterrogationBatchServiceImpl implements InterrogationBatchService {

    private final InterrogationBatchRepository batchRepository;

    @Transactional
    @Override
    public void saveInterrogations(List<Interrogation> interrogations) {
        interrogations.forEach(this::saveInterrogation);
    }

    @Transactional
    @Override
    public void saveInterrogation(Interrogation interrogation) {
        batchRepository.upsert(interrogation);
    }
}