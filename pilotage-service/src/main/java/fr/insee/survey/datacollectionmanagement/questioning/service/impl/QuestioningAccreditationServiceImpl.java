package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestioningAccreditationServiceImpl implements QuestioningAccreditationService {

    private final QuestioningAccreditationRepository questioningAccreditationRepository;
    private final PartitioningService partitioningService;

    public List<QuestioningAccreditation> findByContactIdentifier(String id) {
        return questioningAccreditationRepository.findByIdContact(id);
    }


    @Override
    public Page<QuestioningAccreditation> findAll(Pageable pageable) {
        return questioningAccreditationRepository.findAll(pageable);
    }

    @Override
    public QuestioningAccreditation findById(Long id) {
        return questioningAccreditationRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("QuestioningAccreditation %s not found", id)));
    }

    @Override
    public QuestioningAccreditation saveQuestioningAccreditation(QuestioningAccreditation questioningAccreditation) {
        return questioningAccreditationRepository.save(questioningAccreditation);
    }

    @Override
    public void deleteAccreditation(QuestioningAccreditation acc) {
        questioningAccreditationRepository.deleteById(acc.getId());
    }

}
