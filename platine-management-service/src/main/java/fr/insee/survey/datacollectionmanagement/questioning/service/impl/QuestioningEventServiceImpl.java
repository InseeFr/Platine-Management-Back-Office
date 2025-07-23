package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.LastQuestioningEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ExpertEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningEventRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuestioningEventServiceImpl implements QuestioningEventService {

    private final LastQuestioningEventComparator lastQuestioningEventComparator;

    private final QuestioningEventRepository questioningEventRepository;

    private final QuestioningRepository questioningRepository;

    private final ModelMapper modelMapper;

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
    public Optional<QuestioningEvent> getLastQuestioningEvent(Questioning questioning, List<TypeQuestioningEvent> events) {
        return questioning
                .getQuestioningEvents()
                .stream()
                .filter(qe -> events.contains(qe.getType()))
                .min(lastQuestioningEventComparator);
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
        return questioningEventRepository.countByUploadId(idupload);
    }

    @Override
    public List<QuestioningEventDto> getQuestioningEventsByQuestioningId(UUID questioningId) {
        List<QuestioningEvent> events = questioningEventRepository.findByQuestioningId(questioningId);
        return events.stream().map(qe -> modelMapper.map(qe, QuestioningEventDto.class)).toList();
    }


    public QuestioningEventDto convertToDto(QuestioningEvent questioningEvent) {
        return modelMapper.map(questioningEvent, QuestioningEventDto.class);
    }

    public QuestioningEvent convertToEntity(QuestioningEventDto questioningEventDto) {
        return modelMapper.map(questioningEventDto, QuestioningEvent.class);
    }

    @Override
    public boolean postQuestioningEvent(String eventType, QuestioningEventInputDto questioningEventInputDto) {

        UUID questioningId = questioningEventInputDto.getQuestioningId();
        Questioning questioning = questioningRepository.findById(questioningId)
                .orElseThrow(() -> new NotFoundException(String.format("Questioning %s does not exist", questioningId)));

        List<QuestioningEvent> sameTypeQuestioningEvents = questioningEventRepository.findByQuestioningIdAndType(questioningId, TypeQuestioningEvent.valueOf(eventType));

        if (sameTypeQuestioningEvents.size() > 1) {
            throw new TooManyValuesException(String.format("%s %s questioningEvents found for questioningId %s  - only 1 questioningEvents should be found", sameTypeQuestioningEvents.size(), eventType, questioningId));
        }
        if (!sameTypeQuestioningEvents.isEmpty()) {
            return false;
        }
        QuestioningEvent newQuestioningEvent = new QuestioningEvent();
        newQuestioningEvent.setQuestioning(questioning);
        newQuestioningEvent.setType(TypeQuestioningEvent.valueOf(eventType));
        newQuestioningEvent.setPayload(questioningEventInputDto.getPayload());
        newQuestioningEvent.setDate(questioningEventInputDto.getDate());
        newQuestioningEvent.setPayload(questioningEventInputDto.getPayload());
        questioningEventRepository.save(newQuestioningEvent);
        return true;
    }

    @Override
    public void postExpertEvent(UUID id, ExpertEventDto expertEventDto) {
        Questioning questioning = questioningRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Questioning %s not found", id)));
        questioning.setScore(expertEventDto.score());
        questioning.setScoreInit(expertEventDto.scoreInit());
        questioningRepository.save(questioning);

        Set<QuestioningEvent> events = questioning.getQuestioningEvents();
        QuestioningEvent lastEvent = Optional.ofNullable(events)
                .orElse(Collections.emptySet())
                .stream()
                .filter(qe -> TypeQuestioningEvent.EXPERT_EVENTS.contains(qe.getType()))
                .max(Comparator.comparing(QuestioningEvent::getDate))
                .orElse(null);

        QuestioningEvent candidate = new QuestioningEvent();
        candidate.setQuestioning(questioning);
        candidate.setType(expertEventDto.type());
        candidate.setDate(new Date());

        if (lastEvent == null &&
                (candidate.getType() == TypeQuestioningEvent.EXPERT
                        || candidate.getType() == TypeQuestioningEvent.VALID)) {
            questioningEventRepository.save(candidate);
        }

        if (lastEvent != null
                && candidate.getType() != lastEvent.getType()) {
            questioningEventRepository.save(candidate);
        }
    }


}
