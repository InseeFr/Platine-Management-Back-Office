package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.LastQuestioningEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningEventRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestioningEventServiceImpl implements QuestioningEventService {

    private final LastQuestioningEventComparator lastQuestioningEventComparator;

    private final QuestioningEventRepository questioningEventRepository;

    @Override
    public QuestioningEvent findbyId(Long id) {
        return questioningEventRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("QuestioningEvent %s not found", id)));
    }

    @Override
    public QuestioningEvent saveQuestioningEvent(QuestioningEvent questioningEvent) {
        return questioningEventRepository.save(questioningEvent);
    }

    @Override
    public void deleteQuestioningEvent(Long id) {
        questioningEventRepository.deleteById(id);

    }

    @Override
    public Optional<QuestioningEvent> getLastQuestioningEvent(Questioning questioning,
                                                              List<TypeQuestioningEvent> events) {

        List<QuestioningEvent> listQuestioningEvent = questioning.getQuestioningEvents().stream()
                .filter(qe -> events.contains(qe.getType())).sorted(lastQuestioningEventComparator).toList();
        return listQuestioningEvent.stream().findFirst();
    }

    @Override
    public Long countIdUploadInEvents(Long idupload) {
        return questioningEventRepository.countByUploadId(idupload);
    }

}
