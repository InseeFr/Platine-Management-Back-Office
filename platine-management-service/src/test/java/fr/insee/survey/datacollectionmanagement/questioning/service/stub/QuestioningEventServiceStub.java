package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ExpertEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Setter
public class QuestioningEventServiceStub implements QuestioningEventService {

    private boolean containsQuestioningEvents;

    private List<QuestioningEventDto> questioningEvents = new ArrayList<>();

    @Override
    public QuestioningEvent findbyId(Long id) {
        return null;
    }

    @Override
    public QuestioningEvent saveQuestioningEvent(QuestioningEvent questioningEvent) {
        return null;
    }

    @Override
    public void deleteQuestioningEvent(Long id, boolean launchRefreshHighestEvent) {
        // not used
    }

    @Override
    public void deleteQuestioningEvent(Long id) {
        deleteQuestioningEvent(id, false);
    }

    @Override
    public Optional<QuestioningEvent> getLastQuestioningEvent(Questioning questioning, List<TypeQuestioningEvent> events) {
        return Optional.empty();
    }

    @Override
    public boolean containsTypeQuestioningEvents(List<QuestioningEventDto> events, List<TypeQuestioningEvent> typeEvents) {
        return events
                .stream()
                .map(QuestioningEventDto::getType)
                .map(TypeQuestioningEvent::valueOf)
                .anyMatch(typeEvents::contains);
    }

    @Override
    public Long countIdUploadInEvents(Long idupload) {
        return 0L;
    }

    @Override
    public List<QuestioningEventDto> getQuestioningEventsByQuestioningId(UUID questioningId) {
        return questioningEvents;
    }

    @Override
    public QuestioningEventDto convertToDto(QuestioningEvent questioningEvent) {
        return null;
    }

    @Override
    public QuestioningEvent convertToEntity(QuestioningEventDto questioningEventDto) {
        return null;
    }

    @Override
    public boolean postQuestioningEvent(String eventType, QuestioningEventInputDto questioningEventInputDto) {
        return false;
    }

    @Override
    public void postExpertEvent(UUID id, ExpertEventDto expertEventDto) {
        //not used
    }
}
