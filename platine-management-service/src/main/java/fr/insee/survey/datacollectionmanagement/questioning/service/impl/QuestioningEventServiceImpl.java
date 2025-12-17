package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.exception.CsvFileProcessingException;
import fr.insee.survey.datacollectionmanagement.exception.ForbiddenAccessException;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.InterrogationEventComparator;
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
import fr.insee.survey.datacollectionmanagement.questioning.service.component.ExpertEventComponent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import wiremock.com.ethlo.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestioningEventServiceImpl implements QuestioningEventService {

    public static final String SURVEY_UNIT_ID = "ID_UNITE_ENQUETEE";

    private final LastQuestioningEventComparator lastQuestioningEventComparator;

    private final QuestioningEventRepository questioningEventRepository;

    private final QuestioningRepository questioningRepository;

    private final ModelMapper modelMapper;

    private final InterrogationEventComparator interrogationEventComparator;

    private final ExpertEventComponent expertEventComponent;

    private static final String QUESTIONING_NOT_FOUND_MESSAGE = "Questioning %s not found";

    private final ObjectMapper objectMapper;

    @Override
    public QuestioningEvent findbyId(Long id) {
        return questioningEventRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("QuestioningEvent %s not found", id)));
    }

    @Override
    public QuestioningEvent saveQuestioningEvent(QuestioningEvent questioningEvent) {
        UUID questioningId = questioningEvent.getQuestioning().getId();
        Questioning questioning = questioningRepository.findById(questioningId)
                .orElseThrow(() -> new NotFoundException(String.format(QUESTIONING_NOT_FOUND_MESSAGE, questioningId)));
        questioningEvent.setQuestioning(questioning);
        QuestioningEvent questioningEventSaved = questioningEventRepository.save(questioningEvent);

        // Update the bidirectional link
        questioning.getQuestioningEvents().add(questioningEventSaved);
        refreshHighestEvent(questioningId);
        return questioningEventSaved;
    }

    @Override
    public void deleteQuestioningEvent(Long id) {
        QuestioningEvent questioningEvent = findbyId(id);
        Questioning questioning = questioningEvent.getQuestioning();
        questioning.getQuestioningEvents().remove(questioningEvent);
        UUID questioningId = questioning.getId();
        questioningEventRepository.deleteById(id);
        refreshHighestEvent(questioningId);
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
        newQuestioningEvent = questioningEventRepository.save(newQuestioningEvent);

        // Update the bidirectional link
        questioning.getQuestioningEvents().add(newQuestioningEvent);
        refreshHighestEvent(questioningId);
        return true;
    }

    @Override
    public void postExpertEvent(UUID id, ExpertEventDto expertEventDto) {
        Questioning questioning = questioningRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(QUESTIONING_NOT_FOUND_MESSAGE, id)));
        questioning.setScore(expertEventDto.score());
        questioning.setScoreInit(expertEventDto.scoreInit());
        questioningRepository.save(questioning);

        QuestioningEvent lastExpertEvent = expertEventComponent.getLastExpertEvent(questioning);

        TypeQuestioningEvent newType = expertEventDto.type();
        boolean shouldSaveNewEvent =
                (lastExpertEvent == null && expertEventComponent.isInitialExpertEventAllowed(newType))
                        || (lastExpertEvent != null && expertEventComponent.isTransitionAllowed(lastExpertEvent.getType(), newType));

        if (!shouldSaveNewEvent) {
            log.info("Expert event {} has not been saved", expertEventDto.type());
            return;
        }

        QuestioningEvent created = new QuestioningEvent();
        created.setQuestioning(questioning);
        created.setType(newType);
        created.setDate(new Date());

        created = questioningEventRepository.save(created);

        log.info("New expert event {} has been saved", newType);

        questioning.getQuestioningEvents().add(created);
        refreshHighestEvent(questioning.getId());
    }

    @Override
    public void deleteQuestioningEventIfSpecificRole(List<String> userRoles, Long questioningEventId, TypeQuestioningEvent typeQuestioningEvent)
    {

        if(userRoles.contains(AuthorityRoleEnum.ADMIN.securityRole()))
        {
            deleteQuestioningEvent(questioningEventId);
            return;
        }

        if(userRoles.contains(AuthorityRoleEnum.INTERNAL_USER.securityRole()) && TypeQuestioningEvent.REFUSED_EVENTS.contains(typeQuestioningEvent))
        {
            deleteQuestioningEvent(questioningEventId);
            return;
        }

        throw new ForbiddenAccessException(String.format("User role %s is not allowed to delete questioning event of type %s", userRoles, typeQuestioningEvent));
    }

    public void refreshHighestEvent(UUID questioningId) {
        Questioning questioning = questioningRepository.findById(questioningId)
                .orElseThrow(() -> new NotFoundException(String.format(QUESTIONING_NOT_FOUND_MESSAGE, questioningId)));

        Optional<QuestioningEvent> highestEvent = Optional.ofNullable(questioning.getQuestioningEvents())
                .orElse(Collections.emptySet())
                .stream()
                .filter(qe -> TypeQuestioningEvent.INTERROGATION_EVENTS.contains(qe.getType()))
                .max(interrogationEventComparator);

        questioning.setHighestEventType(highestEvent.map(QuestioningEvent::getType).orElse(null));
        questioning.setHighestEventDate(highestEvent.map(QuestioningEvent::getDate).orElse(null));
        questioningRepository.save(questioning);
    }

    @Override
    public void updatedInterrogationsStatusesFromValpapCsvFile(MultipartFile file) throws NotFoundException, TooManyValuesException{
        final JsonNode payload = objectMapper.createObjectNode()
                .put("source", "platine-gestion");

        final Instant now = Instant.now();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setIgnoreEmptyLines(false)
                .setTrim(false)
                .get();

        try (BufferedReader reader = new BufferedReader(
                                     new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                                     CSVParser csvParser = format.parse(reader)) {

            Set<String> surveyUnitIds = new HashSet<>();
            for (CSVRecord myRecord : csvParser) {
                surveyUnitIds.add(myRecord.get(SURVEY_UNIT_ID));
            }

            if (surveyUnitIds.isEmpty()) {
                throw new CsvFileProcessingException("No value of ID_UNITE_ENQUETEE identifier");
            }

            Set<Questioning> questionings = questioningRepository.findBySurveyUnitIdSuIn(surveyUnitIds);

            Map<String, List<Questioning>> bySu = questionings.stream()
                    .collect(Collectors.groupingBy(q -> q.getSurveyUnit().getIdSu()));

            for (String su : surveyUnitIds) {
                List<Questioning> list = bySu.get(su);
                if (list == null || list.isEmpty()) {
                    throw new NotFoundException(su);
                }
                if (list.size() > 1) {
                    throw new TooManyValuesException(su);
                }
            }

            List<QuestioningEvent> events = new ArrayList<>(surveyUnitIds.size());
            for (String su : surveyUnitIds) {
                Questioning q = bySu.get(su).getFirst();
                QuestioningEvent ev = new QuestioningEvent();
                ev.setQuestioning(q);
                ev.setType(TypeQuestioningEvent.VALPAP);
                ev.setPayload(payload);
                ev.setDate(Date.from(now));
                events.add(ev);
            }
            questioningEventRepository.saveAll(events);
        } catch (IllegalArgumentException | IOException e) {
            throw new CsvFileProcessingException(e.getMessage(), e);
        }
    }
}
