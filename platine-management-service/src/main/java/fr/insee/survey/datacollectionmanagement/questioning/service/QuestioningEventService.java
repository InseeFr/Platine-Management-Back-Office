package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface QuestioningEventService {

    QuestioningEvent findbyId(Long id);

    QuestioningEvent saveQuestioningEvent(QuestioningEvent questioningEvent);

    void deleteQuestioningEvent(Long id);

    /**
     * Get the last event sorted by order of importance among the event types
     * (TypeQuestioningEvent) passed in parameter
     * 
     * @param questioning
     * @param events      list of events to be considered
     * @return optional last Questioning event in order of importance
     */
    Optional<QuestioningEvent> getLastQuestioningEvent(Questioning questioning, List<TypeQuestioningEvent> events);

    Long countIdUploadInEvents(Long idupload);


    QuestioningEventDto convertToDto(QuestioningEvent questioningEvent) ;

    QuestioningEvent convertToEntity(QuestioningEventDto questioningEventDto);

    /**
     * create a questioningEvent
     * @param eventType event type
     * @param questioningEventInputDto input
     * @return true if event is created, false in other cases
     */
    boolean postQuestioningEvent(String eventType, QuestioningEventInputDto questioningEventInputDto);
}
