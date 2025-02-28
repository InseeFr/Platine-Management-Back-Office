package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDtoImpl;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningIdDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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

    private final QuestioningEventService questioningEventService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final QuestioningCommunicationService questioningCommunicationService;

    private final QuestioningCommentService questioningCommentService;

    private final ModelMapper modelMapper;

    private final String lunaticNormalUrl;

    private final String lunaticSensitiveUrl;

    private final String xform1Url;

    private final String xform2Url;

    private static final String PATH_LOGOUT = "pathLogout";
    private static final String PATH_ASSISTANCE = "pathAssistance";

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
    public QuestioningDetailsDto getQuestioningDetails(@PathVariable("id") Long id) {
        Questioning questioning = findById(id);
        return convertToDetailsDto(questioning);
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

    private QuestioningDetailsDto convertToDetailsDto(Questioning questioning) {
        QuestioningDetailsDto questioningDetailsDto = modelMapper.map(questioning, QuestioningDetailsDto.class);
        questioningDetailsDto.setCampaignId(partitioningService.findById(questioning.getIdPartitioning()).getCampaign().getId());
        questioningDetailsDto.setListContactIdentifiers(questioning.getQuestioningAccreditations().stream().map(QuestioningAccreditation::getIdContact).toList());
        Optional<QuestioningEvent> lastQuestioningEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.STATE_EVENTS);
        lastQuestioningEvent.ifPresent(event -> {
            questioningDetailsDto.setLastEvent(event.getType().name());
            questioningDetailsDto.setDateLastEvent(event.getDate());
        });
        Optional<QuestioningCommunication> questioningCommunication = questioningCommunicationService.getLastQuestioningCommunication(questioning);
        questioningCommunication.ifPresent(comm -> {
            questioningDetailsDto.setLastCommunication(comm.getType().name());
            questioningDetailsDto.setDateLastCommunication(comm.getDate());
        });
        Optional<QuestioningEvent> validatedQuestioningEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.VALIDATED_EVENTS);
        validatedQuestioningEvent.ifPresent(event -> questioningDetailsDto.setValidationDate(event.getDate()));
        questioningDetailsDto.setReadOnlyUrl(getReadOnlyUrl(questioning.getIdPartitioning(), questioning.getSurveyUnit().getIdSu()));
        questioningDetailsDto.setListEvents(questioning.getQuestioningEvents().stream().map(questioningEventService::convertToDto).toList());
        questioningDetailsDto.setListCommunications(questioning.getQuestioningCommunications().stream().map(questioningCommunicationService::convertToDto).toList());
        questioningDetailsDto.setListComments(questioning.getQuestioningComments().stream().map(questioningCommentService::convertToOutputDto).toList());
        return questioningDetailsDto;
    }

    public String getReadOnlyUrl(String idPart, String surveyUnitId) throws NotFoundException {

        Partitioning part = partitioningService.findById(idPart);
        Optional<Questioning> optionalQuestioning = findByIdPartitioningAndSurveyUnitIdSu(part.getId(), surveyUnitId);
        if (optionalQuestioning.isPresent()) {
            return getAccessUrl(UserRoles.REVIEWER, optionalQuestioning.get(), part);

        }
        return "";

    }

    @Override
    public QuestionnaireStatusTypeEnum getQuestioningStatus(Questioning questioning, Partitioning part)
    {
        Date today = new Date();
        Date openingDate  = part.getOpeningDate();

        if(today.before(openingDate))
            return QuestionnaireStatusTypeEnum.INCOMING;

        Set<QuestioningEvent> questioningEvents = questioning.getQuestioningEvents();
        Date closingDate = part.getClosingDate();
        boolean refused = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS);

        if(!questioningEvents.isEmpty() && !refused && closingDate.after(today))
        {
            boolean validated = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.VALIDATED_EVENTS);
            boolean opened = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.OPENED_EVENTS);
            if(validated)
                return QuestionnaireStatusTypeEnum.RECEIVED;
            if(opened)
                return QuestionnaireStatusTypeEnum.OPEN;
        }

        return QuestionnaireStatusTypeEnum.NOT_RECEIVED;
    }
}
