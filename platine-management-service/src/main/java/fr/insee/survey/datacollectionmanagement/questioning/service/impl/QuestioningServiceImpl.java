package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.ParametersService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.*;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.InterrogationEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentOutputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningIdDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.questioning.service.builder.QuestioningDetailsDtoBuilder;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class QuestioningServiceImpl implements QuestioningService {

    private final InterrogationEventComparator interrogationEventComparator;

    private final QuestioningRepository questioningRepository;

    private final QuestioningUrlComponent questioningUrlComponent;

    private final SurveyUnitService surveyUnitService;

    private final PartitioningService partitioningService;

    private final ContactService contactService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final ModelMapper modelMapper;

    private final PartitioningRepository partitioningRepository;

    private final ParametersService parametersService;


    @Override
    public Page<Questioning> findAll(Pageable pageable) {
        return questioningRepository.findAll(pageable);
    }

    @Override
    public Questioning findById(Long id) {
        return questioningRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Questioning %s not found", id)));
    }

    @Override
    public Questioning saveQuestioning(Questioning questioning) {
        return questioningRepository.save(questioning);
    }

    @Override
    public void deleteQuestioning(Long id) {
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
    public AssistanceDto getMailAssistanceDto(Long questioningId) {
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
        int nbQuestioningDeleted = 0;
        Set<Questioning> setQuestionings = findByIdPartitioning(partitioning.getId());
        for (Questioning q : setQuestionings) {
            SurveyUnit su = q.getSurveyUnit();
            su.getQuestionings().remove(q);
            surveyUnitService.saveSurveyUnit(su);
            q.getQuestioningEvents().forEach(qe -> questioningEventService.deleteQuestioningEvent(qe.getId()));
            q.getQuestioningAccreditations().forEach(questioningAccreditationService::deleteAccreditation);
            deleteQuestioning(q.getId());
            nbQuestioningDeleted++;
        }
        return nbQuestioningDeleted;
    }

    @Override
    public Set<Questioning> findBySurveyUnitIdSu(String idSu) {
        return questioningRepository.findBySurveyUnitIdSu(idSu);
    }

    @Override
    public Page<SearchQuestioningDto> searchQuestioning(String param, Pageable pageable) {
        if (!StringUtils.isEmpty(param)) {
            List<Questioning> listQuestionings = questioningRepository.findQuestioningByParam(param.toUpperCase());
            List<SearchQuestioningDto> searchDtos = listQuestionings
                    .stream().distinct()
                    .map(this::convertToSearchDto).toList();

            return new PageImpl<>(searchDtos, pageable, searchDtos.size());
        } else {
            Page<Long> idsPage = questioningRepository.findQuestioningIds(pageable);
            List<Questioning> questionings = questioningRepository.findQuestioningsByIds(idsPage.getContent());
            List<SearchQuestioningDto> searchDtos = questionings
                    .stream()
                    .map(this::convertToSearchDto).toList();

            return new PageImpl<>(searchDtos, pageable, idsPage.getTotalElements());
        }
    }

    @Override
    public QuestioningDetailsDto getQuestioningDetails(Long id) {
        Questioning questioning = questioningRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Questioning %s not found", id)));
        Partitioning partitioning = partitioningRepository.findById(questioning.getIdPartitioning())
                .orElseThrow(() -> new NotFoundException(String.format("Partitioning %s not found", questioning.getIdPartitioning())));

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

        QuestioningEventDto highestPriorityEventDto = questioningEventsDto
                .stream()
                .findFirst()
                .orElse(null);

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
                .events(questioningEventsDto, highestPriorityEventDto, validatedEventDto)
                .communications(questioningCommunicationsDto)
                .comments(questioningCommentOutputsDto)
                .readOnlyUrl(readOnlyUrl)
                .build();
    }

    private SearchQuestioningDto convertToSearchDto(Questioning questioning) {
        SearchQuestioningDtoImpl searchQuestioningDto = new SearchQuestioningDtoImpl();
        searchQuestioningDto.setQuestioningId(questioning.getId());
        searchQuestioningDto.setSurveyUnitId(questioning.getSurveyUnit().getIdSu());
        searchQuestioningDto.setSurveyUnitIdentificationCode(questioning.getSurveyUnit().getIdentificationCode());
        searchQuestioningDto.setCampaignId(partitioningService.findById(questioning.getIdPartitioning()).getCampaign().getId());
        searchQuestioningDto.setListContactIdentifiers(questioning.getQuestioningAccreditations().stream().map(QuestioningAccreditation::getIdContact).toList());
        List<QuestioningEventDto> questioningEventsDto = questioning.getQuestioningEvents().stream()
                .filter(qe -> TypeQuestioningEvent.INTERROGATION_EVENTS.contains(qe.getType()))
                .sorted(interrogationEventComparator.reversed())
                .map(event -> modelMapper.map(event, QuestioningEventDto.class))
                .toList();

        Optional<QuestioningEventDto> highestPriorityEventDto = questioningEventsDto.stream().findFirst();
        highestPriorityEventDto.ifPresent(event -> searchQuestioningDto.setLastEvent(event.getType()));
        Optional<QuestioningEventDto> validatedEventDto = questioningEventsDto.stream()
                .filter(qe ->
                        TypeQuestioningEvent.VALIDATED_EVENTS.contains(
                                TypeQuestioningEvent.valueOf(qe.getType())))
                .findFirst();
        validatedEventDto.ifPresent(event -> searchQuestioningDto.setValidationDate(event.getEventDate()));
        Optional<QuestioningCommunication> questioningCommunication = questioning.getQuestioningCommunications().stream().min(Comparator.comparing(QuestioningCommunication::getDate));
        questioningCommunication.ifPresent(comm -> searchQuestioningDto.setLastCommunication(comm.getType().name()));
        return searchQuestioningDto;
    }

    @Override
    public QuestionnaireStatusTypeEnum getQuestioningStatus(Questioning questioning, Partitioning part) {
        Date today = new Date();
        Date openingDate = part.getOpeningDate();

        if (today.before(openingDate)) {
            return QuestionnaireStatusTypeEnum.INCOMING;
        }

        Set<QuestioningEvent> questioningEvents = questioning.getQuestioningEvents();
        Date closingDate = part.getClosingDate();
        boolean refused = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS);

        if (questioningEvents.isEmpty() || refused || !closingDate.after(today))
            return QuestionnaireStatusTypeEnum.NOT_RECEIVED;

        boolean validated = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.VALIDATED_EVENTS);
        boolean opened = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.OPENED_EVENTS);
        boolean started = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.STARTED_EVENTS);


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


}
