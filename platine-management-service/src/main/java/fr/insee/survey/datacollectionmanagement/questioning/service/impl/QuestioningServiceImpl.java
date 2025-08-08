package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SourceTypeEnum;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.ParametersService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.*;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.InterrogationEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.dao.search.SearchQuestioningDao;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.dto.*;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.builder.QuestioningDetailsDtoBuilder;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class QuestioningServiceImpl implements QuestioningService {

    private final InterrogationEventComparator interrogationEventComparator;
    private final QuestioningRepository questioningRepository;
    private final SearchQuestioningDao searchQuestioningDao;
    private final QuestioningUrlComponent questioningUrlComponent;
    private final PartitioningService partitioningService;
    private final ContactService contactService;
    private final QuestioningEventService questioningEventService;
    private final ModelMapper modelMapper;
    private final PartitioningRepository partitioningRepository;
    private final ParametersService parametersService;
    private final SourceRepository sourceRepository;


    @Override
    public Page<Questioning> findAll(Pageable pageable) {
        return questioningRepository.findAll(pageable);
    }

    @Override
    public Questioning findById(UUID id) {
        return questioningRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Questioning %s not found", id)));
    }

    @Override
    public Questioning saveQuestioning(Questioning questioning) {
        return questioningRepository.save(questioning);
    }

    @Override
    public void deleteQuestioning(UUID id) {
        questioningRepository.deleteById(id);
    }

    @Override
    public Set<Questioning> findByIdPartitioning(String idPartitioning) {
        return questioningRepository.findByIdPartitioning(idPartitioning);
    }

    @Override
    public Optional<Questioning> findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning,
                                                                       String surveyUnitIdSu) {
        return questioningRepository.findByIdPartitioningAndSurveyUnitIdSu(idPartitioning,
                surveyUnitIdSu);
    }

    @Override
    public QuestioningIdDto findByCampaignIdAndSurveyUnitIdSu(String campaignId, String surveyUnitIdSu) {
        List<Questioning> listQuestionings = questioningRepository.findQuestioningByCampaignIdAndSurveyUnitId(campaignId, surveyUnitIdSu);
        if (listQuestionings.isEmpty()) {
            throw new NotFoundException(String.format("No questioning found for campaignId %s and surveyUnitId %s", campaignId, surveyUnitIdSu));
        }
        if (listQuestionings.size() > 1) {
            throw new TooManyValuesException(String.format("%s questionings found for campaignId %s and surveyUnitId %s - only 1 questioning should be found", listQuestionings.size(), campaignId, surveyUnitIdSu));
        }

        return new QuestioningIdDto(listQuestionings.getFirst().getId());
    }

    @Override
    public AssistanceDto getMailAssistanceDto(UUID questioningId) {
        Questioning questioning = findById(questioningId);
        String mail = questioning.getAssistanceMail();
        if (StringUtils.isBlank(mail)) {
            Partitioning part = partitioningService.findById(questioning.getIdPartitioning());
            mail = parametersService.findSuitableParameterValue(part, ParameterEnum.MAIL_ASSISTANCE);
        }
        return new AssistanceDto(mail, questioning.getSurveyUnit().getIdSu());
    }


    @Override
    public int deleteQuestioningsOfOnePartitioning(Partitioning partitioning) {
        return questioningRepository.deleteByidPartitioning(partitioning.getId());
    }

    @Override
    public Set<Questioning> findBySurveyUnitIdSu(String idSu) {
        return questioningRepository.findBySurveyUnitIdSu(idSu);
    }

    @Override
    public Slice<SearchQuestioningDto> searchQuestionings(SearchQuestioningParams searchQuestioningParams, Pageable pageable) {
        return searchQuestioningDao.search(searchQuestioningParams, pageable);
    }

    @Override
    public QuestioningDetailsDto getQuestioningDetails(UUID id) {
        Questioning questioning = findById(id);
        Partitioning partitioning = partitioningRepository.findById(questioning.getIdPartitioning())
                .orElseThrow(() -> new NotFoundException(String.format("Partitioning %s not found", questioning.getIdPartitioning())));
        Source source = sourceRepository.findById(partitioning.getCampaign().getSurvey().getSource().getId())
                .orElseThrow(() -> new NotFoundException(String.format("Source %s not found", partitioning.getCampaign().getSurvey().getSource().getId())));

        Boolean isHousehold = SourceTypeEnum.HOUSEHOLD.equals(source.getType());

        SurveyUnit su = questioning.getSurveyUnit();
        QuestioningSurveyUnitDto questioningSurveyUnitDto = new QuestioningSurveyUnitDto(su.getIdSu(), su.getIdentificationCode(), su.getIdentificationName(), su.getLabel());

        String campaignId = partitioning.getCampaign().getId();
        String readOnlyUrl = questioningUrlComponent.getAccessUrl(UserRoles.REVIEWER, questioning, partitioning);

        Map<String, Boolean> contactsIdMain = questioning.getQuestioningAccreditations().stream()
                .collect(Collectors.toMap(
                        QuestioningAccreditation::getIdContact,
                        QuestioningAccreditation::isMain
                ));
        List<QuestioningContactDto> questioningContactDtoList = contactService.findByIdentifiers(contactsIdMain);

        List<QuestioningEventDto> questioningEventsDto = questioning.getQuestioningEvents().stream()
                .filter(qe -> TypeQuestioningEvent.INTERROGATION_EVENTS.contains(qe.getType()))
                .sorted(interrogationEventComparator.reversed())
                .map(event -> modelMapper.map(event, QuestioningEventDto.class))
                .toList();

        QuestioningEventDto validatedEventDto = questioningEventsDto.stream()
                .filter(qe ->
                        TypeQuestioningEvent.VALIDATED_EVENTS.contains(
                                TypeQuestioningEvent.valueOf(qe.getType())))
                .findFirst()
                .orElse(null);

        List<QuestioningCommunicationDto> questioningCommunicationsDto = questioning.getQuestioningCommunications().stream()
                .map(comm -> modelMapper.map(comm, QuestioningCommunicationDto.class))
                .toList();

        Set<QuestioningComment> questioningComments = questioning.getQuestioningComments();
        List<QuestioningCommentOutputDto> questioningCommentOutputsDto = questioningComments.stream()
                .map(comment -> modelMapper.map(comment, QuestioningCommentOutputDto.class))
                .toList();

        return new QuestioningDetailsDtoBuilder()
                .questioningId(id)
                .campaignId(campaignId)
                .surveyUnit(questioningSurveyUnitDto)
                .contacts(questioningContactDtoList)
                .events(questioningEventsDto, questioning.getHighestTypeEvent(), questioning.getHighestDateEvent(), validatedEventDto)
                .communications(questioningCommunicationsDto)
                .comments(questioningCommentOutputsDto)
                .readOnlyUrl(readOnlyUrl)
                .isHousehold(isHousehold)
                .build();
    }

    @Override
    public QuestionnaireStatusTypeEnum getQuestioningStatus(UUID questioningId, Date openingDate, Date closingDate) {
        Date today = new Date();

        if (today.before(openingDate)) {
            return QuestionnaireStatusTypeEnum.INCOMING;
        }
        List<QuestioningEventDto> events = questioningEventService.getQuestioningEventsByQuestioningId(questioningId);

        boolean refused = questioningEventService.containsTypeQuestioningEvents(events, TypeQuestioningEvent.REFUSED_EVENTS);

        if (events.isEmpty() || refused || !closingDate.after(today)) {
            return QuestionnaireStatusTypeEnum.NOT_RECEIVED;
        }

        boolean validated = questioningEventService.containsTypeQuestioningEvents(events, TypeQuestioningEvent.VALIDATED_EVENTS);
        boolean opened = questioningEventService.containsTypeQuestioningEvents(events, TypeQuestioningEvent.OPENED_EVENTS);
        boolean started = questioningEventService.containsTypeQuestioningEvents(events, TypeQuestioningEvent.STARTED_EVENTS);


        if (validated) {
            return QuestionnaireStatusTypeEnum.RECEIVED;
        }
        if (started) {
            return QuestionnaireStatusTypeEnum.IN_PROGRESS;
        }
        if (opened) {
            return QuestionnaireStatusTypeEnum.NOT_STARTED;
        }

        return QuestionnaireStatusTypeEnum.NOT_RECEIVED;
    }

    @Override
    public QuestionnaireStatusTypeEnum getQuestioningStatusFileUpload(Date openingDate, Date closingDate) {
        Date today = new Date();

        if (today.before(openingDate)) {
            return QuestionnaireStatusTypeEnum.INCOMING;
        }

        if (today.after(closingDate)) {
            return QuestionnaireStatusTypeEnum.RECEIVED;
        }

        return QuestionnaireStatusTypeEnum.IN_PROGRESS;
    }

    @Override
    public boolean hasExpertiseStatus(UUID questioningId) {
        Questioning questioning = findById(questioningId);
        TypeQuestioningEvent highestEvent = questioning.getHighestTypeEvent();
        return highestEvent != null && TypeQuestioningEvent.EXPERT_EVENTS.contains(highestEvent);
    }

}
