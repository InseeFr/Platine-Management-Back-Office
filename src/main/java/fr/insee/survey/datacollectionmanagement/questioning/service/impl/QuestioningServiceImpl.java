package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDtoImpl;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.questioning.util.TypeQuestioningEvent;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlTypeEnum.*;

@Service
@RequiredArgsConstructor
public class QuestioningServiceImpl implements QuestioningService {

    private final QuestioningRepository questioningRepository;

    private final SurveyUnitService surveyUnitService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final ModelMapper modelMapper;

    private final ApplicationConfig applicationConfig;

    private static final String PATH_LOGOUT = "pathLogout";
    private static final String PATH_ASSISTANCE = "pathAssistance";

    @Override
    public Page<Questioning> findAll(Pageable pageable) {
        return questioningRepository.findAll(pageable);
    }

    @Override
    public Questioning findbyId(Long id) {
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
    public Questioning findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning,
                                                             String surveyUnitIdSu) {
        return questioningRepository.findByIdPartitioningAndSurveyUnitIdSu(idPartitioning,
                surveyUnitIdSu);
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
     * @param baseUrl      The base URL for the access.
     * @param typeUrl      The type of URL (V1 or V2).
     * @param role         The user role (REVIEWER or INTERVIEWER).
     * @param questioning  The questioning object.
     * @param surveyUnitId The survey unit ID.
     * @return The generated access URL.
     */
    public String getAccessUrl(String baseUrl, String typeUrl, String role, Questioning questioning, String surveyUnitId, String sourceId) {
        // Set default values if baseUrl or typeUrl is empty
        baseUrl = StringUtils.defaultIfEmpty(baseUrl, applicationConfig.getQuestioningUrl());
        typeUrl = StringUtils.defaultIfEmpty(typeUrl, V3.name());

        if (typeUrl.equalsIgnoreCase(V1.name())) {
            return buildV1Url(baseUrl, role, questioning.getModelName(), surveyUnitId);
        }
        if (typeUrl.equalsIgnoreCase(V2.name())) {
            return buildV2Url(baseUrl, role, questioning.getModelName(), surveyUnitId);
        }
        if (typeUrl.equalsIgnoreCase(V3.name())) {
            return buildV3Url(baseUrl, role, questioning.getModelName(), surveyUnitId, sourceId, questioning.getId());
        }

        return "";
    }

    @Override
    public Page<SearchQuestioningDto> searchQuestioning(String param, Pageable pageable) {
        Page<Questioning> pageQuestionings;
        if (!StringUtils.isEmpty(param)) {
            pageQuestionings = questioningRepository.findBySurveyUnitIdSuOrSurveyUnitIdentificationCodeOrQuestioningAccreditationsIdContact(param, param, param, pageable);
        } else {
            pageQuestionings = questioningRepository.findAll(pageable);

        }


        List<SearchQuestioningDto> searchDtos = pageQuestionings
                .stream()
                .map(this::convertToSearchDto).toList();

        return new PageImpl<>(searchDtos, pageable, pageQuestionings.getTotalElements());
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
    protected String buildV1Url(String baseUrl, String role, String campaignId, String surveyUnitId) {
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

    protected String buildV2Url(String baseUrl, String role, String modelName, String surveyUnitId) {
        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            return String.format("%s/readonly/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId);
        }
        if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
            return String.format("%s/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId);
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
    protected String buildV3Url(String baseUrl, String role, String modelName, String surveyUnitId, String sourceId, Long questioningId) {
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
        SearchQuestioningDtoImpl searchQuestioningDto = modelMapper.map(questioning, SearchQuestioningDtoImpl.class);
        searchQuestioningDto.setListContactIdentifiers(questioning.getQuestioningAccreditations().stream().map(QuestioningAccreditation::getIdContact).toList());
        Optional<QuestioningEvent> lastQuestioningEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.STATE_EVENTS);
        lastQuestioningEvent.ifPresent(event -> searchQuestioningDto.setLastEvent(event.getType().name()));
        Optional<QuestioningCommunication> questioningCommunication = questioning.getQuestioningCommunications().stream().sorted(Comparator.comparing(QuestioningCommunication::getDate)).findFirst();
        questioningCommunication.ifPresent(comm -> searchQuestioningDto.setLastCommunication(comm.getType().name()));
        Optional<QuestioningEvent> validatedQuestioningEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.VALIDATED_EVENTS);
        validatedQuestioningEvent.ifPresent(event -> searchQuestioningDto.setValidationDate(event.getDate()));
        return searchQuestioningDto;
    }

}
