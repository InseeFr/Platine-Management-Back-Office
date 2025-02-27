package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.query.dto.*;
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
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
@RequiredArgsConstructor
public class QuestioningServiceImpl implements QuestioningService {

    private final QuestioningRepository questioningRepository;

    private final SurveyUnitService surveyUnitService;

    private final PartitioningService partitioningService;

    private final ContactService contactService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final ModelMapper modelMapper;

    private final String lunaticNormalUrl;

    private final String lunaticSensitiveUrl;

    private final String xform1Url;

    private final String xform2Url;

    private static final String PATH_LOGOUT = "pathLogout";
    private static final String PATH_ASSISTANCE = "pathAssistance";
    private final PartitioningRepository partitioningRepository;

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


    /**
     * Generates an access URL based on the provided parameters.
     *
     * @param role        The user role (REVIEWER or INTERVIEWER).
     * @param questioning The questioning object.
     * @param part        Part of questioning
     * @return The generated access URL.
     */
    public String getAccessUrl(String role, Questioning questioning, Partitioning part) {
        Campaign campaign = part.getCampaign();
        DataCollectionEnum dataCollectionTarget = campaign.getDataCollectionTarget();
        String surveyUnitId = questioning.getSurveyUnit().getIdSu();

        if (dataCollectionTarget == null || dataCollectionTarget.equals(DataCollectionEnum.LUNATIC_NORMAL)) {
            String sourceId = part.getCampaign().getSurvey().getSource().getId().toLowerCase();
            return buildLunaticUrl(lunaticNormalUrl, role, questioning.getModelName(), surveyUnitId, sourceId, questioning.getId());
        }
        if (dataCollectionTarget.equals(DataCollectionEnum.XFORM1)) {
            return buildXformUrl(xform1Url, role, questioning.getModelName(), surveyUnitId);
        }
        if (dataCollectionTarget.equals(DataCollectionEnum.XFORM2)) {
            return buildXformUrl(xform2Url, role, questioning.getModelName(), surveyUnitId);
        }

        if (dataCollectionTarget.equals(DataCollectionEnum.LUNATIC_SENSITIVE)) {
            String sourceId = part.getCampaign().getSurvey().getSource().getId().toLowerCase();
            return buildLunaticUrl(lunaticSensitiveUrl, role, questioning.getModelName(), surveyUnitId, sourceId, questioning.getId());
        }

        return "";
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
        String readOnlyUrl = getAccessUrl(UserRoles.REVIEWER, questioning, partitioning);

        List<String> contactsId = questioning.getQuestioningAccreditations().stream().map(QuestioningAccreditation::getIdContact).toList();
        List<QuestioningContactDto> questioningContactDtoList = contactService.findByIdentifiers(contactsId);

        List<QuestioningEventDto> questioningEventsDto = questioning.getQuestioningEvents().stream()
                .map(event -> modelMapper.map(event, QuestioningEventDto.class))
                .toList();

        Optional<QuestioningEvent> lastEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.STATE_EVENTS);
        QuestioningEventDto lastEventDto = lastEvent
                .map(event -> modelMapper.map(event, QuestioningEventDto.class))
                .orElse(new QuestioningEventDto());

        Optional<QuestioningEvent> validatedEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.VALIDATED_EVENTS);
        QuestioningEventDto validatedEventDto = validatedEvent
                .map(event -> modelMapper.map(event, QuestioningEventDto.class))
                .orElse(new QuestioningEventDto());

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
                .events(questioningEventsDto, lastEventDto, validatedEventDto)
                .communications(questioningCommunicationsDto)
                .comments(questioningCommentOutputsDto)
                .readOnlyUrl(readOnlyUrl)
                .build();
    }


    /**
     * Builds a V1 access URL based on the provided parameters.
     *
     * @param baseUrl      The base URL for the access.
     * @param role         The user role (REVIEWER or INTERVIEWER).
     * @param campaignId   The campaign ID.
     * @param surveyUnitId The survey unit ID.
     * @return The generated V1 access URL.
     */
    protected String buildXformUrl(String baseUrl, String role, String campaignId, String surveyUnitId) {
        if (role.equalsIgnoreCase(UserRoles.REVIEWER)) {
            return String.format("%s/visualiser/%s/%s", baseUrl, campaignId, surveyUnitId);
        }
        if (role.equalsIgnoreCase(UserRoles.INTERVIEWER)) {
            return String.format("%s/repondre/%s/%s", baseUrl, campaignId, surveyUnitId);
        }
        return "";
    }


    /**
     * Builds a V3 access URL based on the provided parameters
     *
     * @param baseUrl      The base URL for the access.
     * @param role         The user role (REVIEWER or INTERVIEWER).
     * @param modelName    The model ID.
     * @param surveyUnitId The survey unit ID.
     * @return The generated V3 access URL.
     */
    protected String buildLunaticUrl(String baseUrl, String role, String modelName, String surveyUnitId, String sourceId, Long questioningId) {
        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            return UriComponentsBuilder.fromHttpUrl(String.format("%s/v3/review/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId)).toUriString();
        }
        if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
            return UriComponentsBuilder.fromHttpUrl(String.format("%s/v3/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId))
                    .queryParam(PATH_LOGOUT, URLEncoder.encode("/" + sourceId, StandardCharsets.UTF_8))
                    .queryParam(PATH_ASSISTANCE, URLEncoder.encode("/" + sourceId + "/contacter-assistance/auth?questioningId=" + questioningId, StandardCharsets.UTF_8))
                    .build().toUriString();
        }
        return "";
    }


    private SearchQuestioningDto convertToSearchDto(Questioning questioning) {
        SearchQuestioningDtoImpl searchQuestioningDto = new SearchQuestioningDtoImpl();
        searchQuestioningDto.setQuestioningId(questioning.getId());
        searchQuestioningDto.setSurveyUnitId(questioning.getSurveyUnit().getIdSu());
        searchQuestioningDto.setSurveyUnitIdentificationCode(questioning.getSurveyUnit().getIdentificationCode());
        searchQuestioningDto.setCampaignId(partitioningService.findById(questioning.getIdPartitioning()).getCampaign().getId());
        searchQuestioningDto.setListContactIdentifiers(questioning.getQuestioningAccreditations().stream().map(QuestioningAccreditation::getIdContact).toList());
        Optional<QuestioningEvent> lastQuestioningEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.STATE_EVENTS);
        lastQuestioningEvent.ifPresent(event -> searchQuestioningDto.setLastEvent(event.getType().name()));
        Optional<QuestioningCommunication> questioningCommunication = questioning.getQuestioningCommunications().stream().min(Comparator.comparing(QuestioningCommunication::getDate));
        questioningCommunication.ifPresent(comm -> searchQuestioningDto.setLastCommunication(comm.getType().name()));
        Optional<QuestioningEvent> validatedQuestioningEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.VALIDATED_EVENTS);
        validatedQuestioningEvent.ifPresent(event -> searchQuestioningDto.setValidationDate(event.getDate()));
        return searchQuestioningDto;
    }

    @Override
    public QuestionnaireStatusTypeEnum getQuestioningStatus(Questioning questioning, Partitioning part)
    {
        Date today = new Date();
        Date openingDate  = part.getOpeningDate();

        if(today.before(openingDate)) {
            return QuestionnaireStatusTypeEnum.INCOMING;
        }

        Set<QuestioningEvent> questioningEvents = questioning.getQuestioningEvents();
        Date closingDate = part.getClosingDate();
        boolean refused = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS);

        if(questioningEvents.isEmpty() || refused || !closingDate.after(today))
            return QuestionnaireStatusTypeEnum.NOT_RECEIVED;

        boolean validated = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.VALIDATED_EVENTS);
        boolean opened = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.OPENED_EVENTS);

        if(validated) {
            return QuestionnaireStatusTypeEnum.RECEIVED;
        }
        if(opened) {
            return QuestionnaireStatusTypeEnum.OPEN;
        }

        return QuestionnaireStatusTypeEnum.NOT_RECEIVED;
    }
}
