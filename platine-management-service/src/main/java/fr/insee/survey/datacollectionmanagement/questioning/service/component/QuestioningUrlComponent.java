package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningUrlContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QuestioningUrlComponent {

    private final String lunaticNormalUrl;

    private final String lunaticSensitiveUrl;

    private final String questionnaireApiUrl;

    private final String questionnaireApiSensitiveUrl;

    private final String xform1Url;

    private final String xform2Url;


    private static final String PATH_ASSISTANCE = "pathAssistance";

    /**
     * Generates an access URL based on the provided parameters.
     * @param role        The user role (REVIEWER or INTERVIEWER).
     * @param questioning The questioning object.
     * @param part        Part of questioning
     * @return The generated access URL.
     */
    public String getAccessUrl(String role, Questioning questioning, Partitioning part) {
        if (questioning == null || part == null) {
            return "";
        }
        Campaign campaign = part.getCampaign();
        Survey survey = campaign.getSurvey();
        Source source = survey.getSource();
        QuestioningUrlContext ctx = new QuestioningUrlContext(
                questioning.getSurveyUnit().getIdSu(),
                questioning.getId(),
                String.format("%s-%s-%s",source.getId().toLowerCase(),survey.getYear(),campaign.getPeriod()),
                campaign.getDataCollectionTarget(),
                source.getId().toLowerCase(),
                survey.getYear(),
                campaign.getPeriod().getValue(),
                campaign.getOperationUploadReference(),
                ""
        );
        return buildAccessUrl(role, ctx);
    }

    /**
     * Generates an access URL based on the provided parameters.
     *
     * @param role        The user role (REVIEWER or INTERVIEWER).
     * @param context Data to build urls
     * @return The generated access URL.
     */
    public String buildAccessUrl(String role, QuestioningUrlContext context) {
        if (context.dataCollection() == null) { //default
            return buildLunaticUrl(role, lunaticNormalUrl, context);
        }

        return switch (context.dataCollection()) {
            case FILE_UPLOAD -> "";
            case LUNATIC_NORMAL ->
                    buildLunaticUrl(role, lunaticNormalUrl, context);
            case LUNATIC_SENSITIVE ->
                    buildLunaticUrl(role, lunaticSensitiveUrl, context);
            case XFORM1 -> buildXformUrl(xform1Url, role, context.campaignId(), context.surveyUnitId());
            case XFORM2 -> buildXformUrl(xform2Url, role, context.campaignId(), context.surveyUnitId());
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
     * @param role     The user role (REVIEWER or INTERVIEWER).
     * @param baseUrl  The base URL for the access.
     * @param context  Data to build urls
     * @return The generated V3 access URL.
     */
    protected String buildLunaticUrl(String role, String baseUrl, QuestioningUrlContext context) {
        if (UserRoles.REVIEWER.equalsIgnoreCase(role)) {
            return UriComponentsBuilder
                    .fromUriString(String.format("%s/v3/review/interrogations/%s", baseUrl, context.questioningId()))
                    .toUriString();
        }
        if (UserRoles.INTERVIEWER.equalsIgnoreCase(role)) {
            String urlAssistance = String.format("/mes-enquetes/%s/contacter-assistance/auth?interrogationId=%s&surveyUnitId=%s&contactId=%s",
                    context.sourceId(), context.questioningId(), context.surveyUnitId(), context.contactId());
            return  UriComponentsBuilder
                    .fromUriString(String.format("%s/v3/interrogations/%s", baseUrl, context.questioningId()))
                    .queryParam(PATH_ASSISTANCE, URLEncoder.encode(urlAssistance, StandardCharsets.UTF_8))
                    .build().toUriString();
        }
        return "";
    }

    /**
     * Builds deposit proof based on the provided parameters
     * @param questioningId questioning id
     * @param dataCollection data collection enum type
     * @return the deosit proof url for the associated questioning
     */
    public String buildDepositProofUrl(UUID questioningId, DataCollectionEnum dataCollection) {
        String path = String.format("/api/interrogations/%s/deposit-proof", questioningId);

        if (DataCollectionEnum.LUNATIC_NORMAL.equals(dataCollection)) {
            return questionnaireApiUrl + path;
        }

        if (DataCollectionEnum.LUNATIC_SENSITIVE.equals(dataCollection)) {
            return questionnaireApiSensitiveUrl + path;
        }

        return null;
    }

    /**
     * Build downloadUrl for file_upload campaign
     * @param context
     * @return
     */
    public String buildDownloadUrl(QuestioningUrlContext context) {
        return switch (context.operationUpload()) {
            case "ofats" -> String.format("insee-%s-%s-%s.xlsx", context.surveyUnitId(), context.operationUpload(), context.surveyYear());
            default -> String.format("%s-%s-%s-%d-%s.xlsx", context.operationUpload(), context.surveyUnitId(), context.sourceId(), context.surveyYear(), context.period());
        };
    }
}
