package fr.insee.survey.datacollectionmanagement.questioning.util;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlRedirectionEnum.POOL1;
import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlRedirectionEnum.POOL2;
import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlTypeEnum.*;

public class QuestioningUrlResolver {
    private static final String PATH_LOGOUT = "pathLogout";
    private static final String PATH_ASSISTANCE = "pathAssistance";

    private final ApplicationConfig applicationConfig;
    private final Map<String, String> urlMap;

    public QuestioningUrlResolver(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.urlMap = initializeUrlMap();
    }



    private Map<String, String> initializeUrlMap() {
        Map<String, String> map = new HashMap<>();
        map.put(V1.name() +"_"+ POOL1.name(), applicationConfig.getQuestioningUrlStromaeV1Pool1());
        map.put(V1.name() +"_"+ POOL2.name(), applicationConfig.getQuestioningUrlStromaeV1Pool2());
        map.put(V2.name() +"_"+ POOL1.name(), applicationConfig.getQuestioningUrlStromaeV2Pool1());
        map.put(V3.name() +"_"+ POOL1.name(), applicationConfig.getQuestioningUrlStromaeV3Pool1());
        return map;
    }

    public String resolveUrl(String typeUrl, String pool) {
        return urlMap.getOrDefault(typeUrl + "_" + pool.toUpperCase(), applicationConfig.getQuestioningUrlStromaeV3Pool1());
    }
    public String buildV1Url(UrlParameters params) {
        String baseUrl = params.getBaseUrl();
        String role = params.getRole();
        String campaignId = params.getCampaignName();
        String surveyUnitId = params.getSurveyUnitId();

        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            return String.format("%s/visualiser/%s/%s", baseUrl, campaignId, surveyUnitId);
        }
        if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
            return String.format("%s/repondre/%s/%s", baseUrl, campaignId, surveyUnitId);
        }
        return "";
    }

    public String buildV2Url(UrlParameters params) {
        String baseUrl = params.getBaseUrl();
        String role = params.getRole();
        String modelName = params.getQuestioning().getModelName();
        String surveyUnitId = params.getSurveyUnitId();

        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            return String.format("%s/readonly/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId);
        }
        if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
            return String.format("%s/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId);
        }
        return "";
    }

    public String buildV3Url(UrlParameters params) {
        String baseUrl = params.getBaseUrl();
        String role = params.getRole();
        String modelName = params.getQuestioning().getModelName();
        String surveyUnitId = params.getSurveyUnitId();
        String sourceId = params.getSourceId();
        Long questioningId = params.getQuestioning().getId();

        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            return UriComponentsBuilder.fromHttpUrl(String.format("%s/review/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId)).toUriString();
        }
        if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
            return UriComponentsBuilder.fromHttpUrl(String.format("%s/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId))
                    .queryParam(PATH_LOGOUT, URLEncoder.encode("/" + sourceId, StandardCharsets.UTF_8))
                    .queryParam(PATH_ASSISTANCE, URLEncoder.encode("/" + sourceId + "/contacter-assistance/auth?questioningId=" + questioningId, StandardCharsets.UTF_8))
                    .build().toUriString();
        }
        return "";
    }
}
