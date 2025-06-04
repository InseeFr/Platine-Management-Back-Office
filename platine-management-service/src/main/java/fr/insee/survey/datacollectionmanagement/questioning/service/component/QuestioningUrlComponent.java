package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class QuestioningUrlComponent {

    private final String lunaticNormalUrl;

    private final String lunaticSensitiveUrl;

    private final String xform1Url;

    private final String xform2Url;

    private static final String PATH_LOGOUT = "pathLogout";
    private static final String PATH_ASSISTANCE = "pathAssistance";
    private static final String PATH_EXIT = "pathExit";

    /**
     * Generates an access URL based on the provided parameters.
     *
     * @param role        The user role (REVIEWER or INTERVIEWER).
     * @param questioning The questioning object.
     * @param part        Part of questioning
     * @return The generated access URL.
     */
    public String getAccessUrl(String role, Questioning questioning, Partitioning part) {
        return getAccessUrlWithContactId(role, questioning, part, "");
    }

    /**
     * Generates an access URL based on the provided parameters.
     *
     * @param role        The user role (REVIEWER or INTERVIEWER).
     * @param questioning The questioning object.
     * @param part        Part of questioning
     * @param contactId   Contact id
     * @return The generated access URL.
     */
    public String getAccessUrlWithContactId(String role, Questioning questioning, Partitioning part, String contactId) {
        String modelName = questioning.getModelName();
        String surveyUnitId = questioning.getSurveyUnit().getIdSu();
        Long questioningId = questioning.getId();
        Campaign campaign = part.getCampaign();
        DataCollectionEnum target = campaign.getDataCollectionTarget();
        String sourceId = campaign.getSurvey().getSource().getId().toLowerCase();
        String campaignId = sourceId + "-" + campaign.getSurvey().getYear() + "-" + campaign.getPeriod();


        return switch (target != null ? target : DataCollectionEnum.LUNATIC_NORMAL) {
            case LUNATIC_NORMAL ->
                    buildLunaticUrl(role, lunaticNormalUrl, modelName, surveyUnitId, sourceId, questioningId, contactId);
            case LUNATIC_SENSITIVE ->
                    buildLunaticUrl(role, lunaticSensitiveUrl, modelName, surveyUnitId, sourceId, questioningId, contactId);
            case XFORM1 -> buildXformUrl(xform1Url, role, campaignId, surveyUnitId);
            case XFORM2 -> buildXformUrl(xform2Url, role, campaignId, surveyUnitId);
        };
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
        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            return String.format("%s/visualiser/%s/%s", baseUrl, campaignId, surveyUnitId);
        }
        if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
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
    protected String buildLunaticUrl(String role, String baseUrl, String modelName, String surveyUnitId, String sourceId, Long questioningId, String contactId) {
        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            return UriComponentsBuilder
                    .fromUriString(String.format("%s/v3/review/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId))
                    .toUriString();
        }
        if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
            String urlAssistance = String.format("/mes-enquetes/%s/contacter-assistance/auth?questioningId=%s&surveyUnitId=%s&contactId=%s",
                    sourceId, questioningId, surveyUnitId, contactId);
            return UriComponentsBuilder.fromUriString(String.format("%s/v3/questionnaire/%s/unite-enquetee/%s", baseUrl, modelName, surveyUnitId))
                    .queryParam(PATH_LOGOUT, URLEncoder.encode("/deconnexion", StandardCharsets.UTF_8))
                    .queryParam(PATH_EXIT, URLEncoder.encode("/mes-enquetes", StandardCharsets.UTF_8))
                    .queryParam(PATH_ASSISTANCE, URLEncoder.encode(urlAssistance, StandardCharsets.UTF_8))
                    .build().toUriString();
        }
        return "";
    }
}
