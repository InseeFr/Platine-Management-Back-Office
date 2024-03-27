package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuestioningAccreditationServiceImpl implements QuestioningAccreditationService {

    private final QuestioningAccreditationRepository questioningAccreditationRepository;

    public List<QuestioningAccreditation> findByContactIdentifier(String id) {
        return questioningAccreditationRepository.findByIdContact(id);
    }

    public Set<QuestioningAccreditation> findBySurveyUnit(SurveyUnit su) {
        Set<QuestioningAccreditation> setReturn = new HashSet<>();
        for (Questioning qu : su.getQuestionings()) {
            setReturn.addAll(qu.getQuestioningAccreditations());
        }
        return setReturn;
    }

    @Override
    public Page<QuestioningAccreditation> findAll(Pageable pageable) {
        return questioningAccreditationRepository.findAll(pageable);
    }

    @Override
    public QuestioningAccreditation findById(Long id) {
        return questioningAccreditationRepository.findById(id).orElseThrow(()-> new NotFoundException(String.format("QuestioningAccreditation %s not found", id)));
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
