package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.InterrogationEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QuestioningEventComponent {

    private final QuestioningRepository questioningRepository;
    private final InterrogationEventComparator interrogationEventComparator;

    @Transactional
    public void refreshHighestEvent(UUID questioningId) {
        Questioning questioning = questioningRepository.findById(questioningId)
                .orElseThrow(() -> new NotFoundException(String.format("Questioning %s not found", questioningId)));

        Optional<QuestioningEvent> highestEvent = Optional.ofNullable(questioning.getQuestioningEvents())
                .orElse(Collections.emptySet())
                .stream()
                .filter(qe -> TypeQuestioningEvent.INTERROGATION_EVENTS.contains(qe.getType()))
                .max(interrogationEventComparator);

        questioning.setHighestTypeEvent(highestEvent.map(QuestioningEvent::getType).orElse(null));
        questioning.setHighestDateEvent(highestEvent.map(QuestioningEvent::getDate).orElse(null));
        questioningRepository.save(questioning);
    }
}
