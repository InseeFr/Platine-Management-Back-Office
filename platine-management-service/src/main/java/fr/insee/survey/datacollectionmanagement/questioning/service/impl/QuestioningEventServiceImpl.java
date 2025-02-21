package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.LastQuestioningEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ValidatedQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningEventRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    public QuestioningEventDto convertToDto(QuestioningEvent questioningEvent) {
        return modelMapper.map(questioningEvent, QuestioningEventDto.class);
    }

    public QuestioningEvent convertToEntity(QuestioningEventDto questioningEventDto) {
        return modelMapper.map(questioningEventDto, QuestioningEvent.class);
    }

    @Override
    public boolean postValintQuestioningEvent(ValidatedQuestioningEventDto validatedQuestioningEventDto) {

        Long questioningId = validatedQuestioningEventDto.getQuestioningId();
        Questioning questioning = questioningRepository.findById(questioningId)
                .orElseThrow(() -> new NotFoundException(String.format("Questioning %s does not exist", questioningId)));

        List<QuestioningEvent> valintQuestioningEvents = questioningEventRepository.findByQuestioningIdAndType(questioningId, TypeQuestioningEvent.VALINT);

        if (!valintQuestioningEvents.isEmpty() && valintQuestioningEvents.size()>1) {
            throw new TooManyValuesException(String.format("%s VALINT questioningEvents found for questioningId %s  - only 1 questioningEvents should be found", valintQuestioningEvents.size(), questioningId));
        }
        if (!valintQuestioningEvents.isEmpty()) {
            QuestioningEvent valintQuestioningEvent = valintQuestioningEvents.getFirst();
            valintQuestioningEvent.setDate(validatedQuestioningEventDto.getDate());
            valintQuestioningEvent.setPayload(validatedQuestioningEventDto.getPayload());
            questioningEventRepository.save(valintQuestioningEvent);
            return false;
        }
        QuestioningEvent valintQuestioningEvent = new QuestioningEvent();
        valintQuestioningEvent.setQuestioning(questioning);
        valintQuestioningEvent.setType(TypeQuestioningEvent.VALINT);
        valintQuestioningEvent.setPayload(validatedQuestioningEventDto.getPayload());
        valintQuestioningEvent.setDate(validatedQuestioningEventDto.getDate());
        valintQuestioningEvent.setPayload(validatedQuestioningEventDto.getPayload());
        questioningEventRepository.save(valintQuestioningEvent);
        return true;
    }
}
