package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;

import java.util.List;
import java.util.Optional;

public class QuestioningEventServiceStub implements QuestioningEventService {

    @Override
    public QuestioningEvent findbyId(Long id) {
        return null;
    }

    @Override
    public QuestioningEvent saveQuestioningEvent(QuestioningEvent questioningEvent) {
        return null;
    }

    @Override
    public void deleteQuestioningEvent(Long id) {

    }

    @Override
    public Optional<QuestioningEvent> getLastQuestioningEvent(Questioning questioning, List<TypeQuestioningEvent> events) {
        return Optional.empty();
    }

    @Override
    public Long countIdUploadInEvents(Long idupload) {
        return 0L;
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
    public boolean postValintQuestioningEvent(String eventType, QuestioningEventInputDto questioningEventInputDto) {
        return false;
    }
}
