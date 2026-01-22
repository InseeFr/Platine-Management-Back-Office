package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.exception.*;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.InterrogationEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.LastQuestioningEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ExpertEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusEvent;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
        QuestioningEvent entity = modelMapper.map(questioningEventDto, QuestioningEvent.class);
        if (entity.getStatus() == null) {
            entity.setStatus(StatusEvent.AUTOMATIC);
        }
        return entity;
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

        newQuestioningEvent.setStatus(questioningEventInputDto.getStatus() != null
                ? questioningEventInputDto.getStatus()
                : StatusEvent.AUTOMATIC);

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

        created.setStatus(expertEventDto.status() != null
                ? expertEventDto.status()
                : StatusEvent.AUTOMATIC);

        created = questioningEventRepository.save(created);

        log.info("New expert event {} has been saved", newType);

        questioning.getQuestioningEvents().add(created);
        refreshHighestEvent(questioning.getId());
    }

    @Override
    public void deleteQuestioningEventIfSpecificRoleAndManualStatus(List<String> userRoles, Long questioningEventId) {

        QuestioningEvent event = questioningEventRepository.findById(questioningEventId)
                .orElseThrow(() -> new NotFoundException(String.format("QuestioningEvent %s not found", questioningEventId)));

        boolean isAdmin = userRoles.contains(AuthorityRoleEnum.ADMIN.securityRole());

        if (StatusEvent.AUTOMATIC.equals(event.getStatus()) && !isAdmin) {
            throw new ForbiddenAccessException(
                    String.format("Deletion of automatic event %s is forbidden", questioningEventId)
            );
        }

        boolean isInternalUserAllowed = userRoles.contains(AuthorityRoleEnum.INTERNAL_USER.securityRole())
                && StatusEvent.MANUAL.equals(event.getStatus());

        if (!isAdmin && !isInternalUserAllowed) {
            throw new ForbiddenAccessException(
                    String.format("User role %s is not allowed to delete questioning event of status %s", userRoles, event.getStatus())
            );
        }

        deleteQuestioningEvent(questioningEventId);
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
    public void bulkUploadRecupapInterrogationEvents(String campaignId, MultipartFile file) throws InterrogationNotFoundException, TooManyInterrogationsException{
        final JsonNode payload = objectMapper.createObjectNode().put("source", "platine-gestion");
        Date nowDate = new Date();

        try {
            Set<String> surveyUnitIds = readSurveyUnitIdsFromCsv(file);
            Map<String, List<Questioning>> questioningBySu = getQuestioningsBySuInCampaign(surveyUnitIds, campaignId);

            validateSuInQuestionings(questioningBySu, surveyUnitIds);

            List<QuestioningEvent> events = buildRecupapInterrogationEvents(questioningBySu, payload, nowDate);
            questioningEventRepository.saveAll(events);
            questioningEventRepository.flush();
            events
                    .stream()
                    .map(qe -> qe.getQuestioning().getId())
                    .distinct()
                    .forEach(this::refreshHighestEvent);

        } catch (IllegalArgumentException | IOException e) {
            throw new CsvFileProcessingException(e.getMessage(), e);
        }
    }

    private Set<String> readSurveyUnitIdsFromCsv(MultipartFile file) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setIgnoreEmptyLines(false)
                .setTrim(false)
                .get();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            CSVParser csvParser = format.parse(reader)) {
            Set<String> surveyUnitIds = new HashSet<>();
            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            Set<String> headers = headerMap == null ? Collections.emptySet() : headerMap.keySet();

            if (!headers.contains(SURVEY_UNIT_ID)) {
                throw new IllegalArgumentException(
                        String.format("The column name %s is incorrect", headers)
                );
            }

            for (CSVRecord listSu : csvParser) {
                String surveyUnitId = listSu.get(SURVEY_UNIT_ID);
                if (surveyUnitId == null || surveyUnitId.isBlank()) {
                    log.warn("SurveyUnitId is blank");
                    continue;
                }
                surveyUnitIds.add(surveyUnitId.trim());
            }
            if (surveyUnitIds.isEmpty()) {
                throw new CsvFileProcessingException("No value of ID_UNITE_ENQUETEE identifier");
            }
            return surveyUnitIds;
        }
    }

    private Map<String, List<Questioning>> getQuestioningsBySuInCampaign(Set<String> surveyUnitIds, String campaignId) {
        Set<Questioning> questionings = questioningRepository.findBySurveyUnitIdSuInAndCampaignIdAndOpen(surveyUnitIds, campaignId);
        if (questionings == null) {
            throw new IllegalArgumentException("Questionings result is null");
        }
        return questionings.stream().collect(Collectors.groupingBy(q -> q.getSurveyUnit().getIdSu()));
    }

    private void validateSuInQuestionings(
            Map<String, List<Questioning>> questionningBySu,
            Set<String> surveyUnitIds
    ) throws InterrogationNotFoundException, TooManyInterrogationsException {

        for (String su : surveyUnitIds) {
            List<Questioning> list = questionningBySu.get(su);

            if (list == null || list.isEmpty()) {
                throw new InterrogationNotFoundException(su);
            }

            if (list.size() > 1) {
                throw new TooManyInterrogationsException(su);
            }
        }
    }

    private List<QuestioningEvent> buildRecupapInterrogationEvents(
            Map<String, List<Questioning>> questionningBySu,
            JsonNode payload,
            Date nowDate) {

        int estimatedSize = questionningBySu.values().stream().mapToInt(List::size).sum();
        List<QuestioningEvent> events = new ArrayList<>(estimatedSize);

        for (List<Questioning> questionings : questionningBySu.values()) {
            for (Questioning questioning : questionings) {
                QuestioningEvent event = new QuestioningEvent();
                event.setQuestioning(questioning);
                event.setType(TypeQuestioningEvent.RECUPAP);
                event.setPayload(payload);
                event.setDate(nowDate);
                event.setStatus(StatusEvent.MANUAL);

                events.add(event);
            }
        }
        return events;
    }

}
