package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.questioning.util.QuestioningUrlResolver;
import fr.insee.survey.datacollectionmanagement.questioning.util.UrlParameters;
import fr.insee.survey.datacollectionmanagement.questioning.util.UrlTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlRedirectionEnum.POOL1;
import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlTypeEnum.V3;

@Service
public class QuestioningServiceImpl implements QuestioningService {

    private final QuestioningRepository questioningRepository;

    private final SurveyUnitService surveyUnitService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final ApplicationConfig applicationConfig;

    QuestioningUrlResolver urlResolver;

    public QuestioningServiceImpl(QuestioningRepository questioningRepository, SurveyUnitService surveyUnitService, QuestioningEventService questioningEventService, QuestioningAccreditationService questioningAccreditationService, ApplicationConfig applicationConfig) {
        this.questioningRepository = questioningRepository;
        this.surveyUnitService = surveyUnitService;
        this.questioningEventService = questioningEventService;
        this.questioningAccreditationService = questioningAccreditationService;
        this.applicationConfig = applicationConfig;
        urlResolver = new QuestioningUrlResolver(applicationConfig);
    }


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
     * @param pool         The pool for access
     * @param typeUrl      The type of URL (V1 or V2 or V3).
     * @param role         The user role (REVIEWER or INTERVIEWER).
     * @param questioning  The questioning object.
     * @param part         The part of questioning
     * @return The generated access URL.
     */
    public String getAccessUrl(String pool, String typeUrl, String role, Questioning questioning, Partitioning part) {
        pool = StringUtils.defaultIfEmpty(pool, POOL1.name());
        typeUrl = StringUtils.defaultIfEmpty(typeUrl, V3.name());

        String baseUrl = urlResolver.resolveUrl(typeUrl, pool);
        String sourceId = part.getCampaign().getSurvey().getSource().getId();
        UrlParameters params = new UrlParameters(baseUrl, role, questioning, questioning.getSurveyUnit().getIdSu(), sourceId, "");
        UrlTypeEnum typeUrlEnum = UrlTypeEnum.valueOf(typeUrl);

        switch (typeUrlEnum) {
            case V1:
                String campaignName = part.getCampaign().getSurvey().getSource().getId() + "-" + part.getCampaign().getSurvey().getYear() + "-" + part.getCampaign().getPeriod();
                params.setCampaignName(campaignName);
                return urlResolver.buildV1Url(params);
            case V2:
                return urlResolver.buildV2Url(params);
            default:
                return urlResolver.buildV3Url(params);
        }
    }
}
