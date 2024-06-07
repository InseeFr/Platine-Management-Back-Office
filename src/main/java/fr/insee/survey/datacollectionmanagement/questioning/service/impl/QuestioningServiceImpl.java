package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlTypeEnum.*;

@Service
@RequiredArgsConstructor
public class QuestioningServiceImpl implements QuestioningService {

    private final QuestioningRepository questioningRepository;

    private final SurveyUnitService surveyUnitService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningAccreditationService questioningAccreditationService;

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
            q.getQuestioningEvents().stream().forEach(qe -> questioningEventService.deleteQuestioningEvent(qe.getId()));
            q.getQuestioningAccreditations().stream()
                    .forEach(questioningAccreditationService::deleteAccreditation);
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
     * @param role          The user role (REVIEWER or INTERVIEWER).
     * @param questioning   The questioning object.
     * @param surveyUnitId  The survey unit ID.
     * @return The generated access URL.
     */
    public String getAccessUrl(String baseUrl, String typeUrl, String role, Questioning questioning, String surveyUnitId, String sourceId) {
        // Set default values if baseUrl or typeUrl is empty
        baseUrl = StringUtils.defaultIfEmpty(baseUrl, applicationConfig.getQuestioningUrl());
        typeUrl = StringUtils.defaultIfEmpty(typeUrl, V2.name());

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



    /**
     * Builds a V1 access URL based on the provided parameters.
     *
     * @param baseUrl      The base URL for the access.
     * @param role          The user role (REVIEWER or INTERVIEWER).
     * @param campaignId    The campaign ID.
     * @param surveyUnitId  The survey unit ID.
     * @return The generated V1 access URL.
     */
    protected String buildV1Url(String baseUrl, String role, String campaignId, String surveyUnitId) {
        String url ="";
        if (role.equalsIgnoreCase(UserRoles.REVIEWER)) {
            url = String.format("%s/visualiser/%s/%s", baseUrl, campaignId, surveyUnitId);
        } else if (role.equalsIgnoreCase(UserRoles.INTERVIEWER)) {
            url = String.format("%s/repondre/%s/%s", baseUrl, campaignId, surveyUnitId);
        }
        return url;
    }

    /**
     * Builds a V3 access URL based on the provided parameters
     * @param baseUrl host url
     * @param role
     * @param modelName
     * @param surveyUnitId
     * @return The generated V3 access URL.
     */

    protected String buildV2Url(String baseUrl, String role, String modelName, String surveyUnitId) {
        String url = "";
        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            url = String.format("%s/readonly/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId);
        } else if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
            url = String.format("%s/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId);
        }
        return url;
    }

    /**
     * Builds a V3 access URL based on the provided parameters
     * @param baseUrl
     * @param role
     * @param modelName
     * @param surveyUnitId
     * @param sourceId
     * @return The generated V3 access URL.
     */
    protected String buildV3Url(String baseUrl, String role, String modelName, String surveyUnitId, String sourceId, Long questioningId) {
        String url = "";
        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            url = UriComponentsBuilder.fromHttpUrl(String.format("%s/v3/readonly/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId)).toUriString();
        } else if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
            url = UriComponentsBuilder.fromHttpUrl(String.format("%s/v3/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId))
                    .queryParam(PATH_LOGOUT, "/" + sourceId)
                    .queryParam(PATH_ASSISTANCE, URLDecoder.decode("/" + sourceId + "/contacter-assistance/auth?questioningId=" + questioningId, StandardCharsets.UTF_8))
                    .build().toUriString();
        }
        return url;
    }

 }
