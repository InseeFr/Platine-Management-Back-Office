package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningUrlContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

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
    private static final String SURVEY_UNIT_LABEL = "surveyUnitLabel";
    private static final String SURVEY_UNIT_COMPOSITE_NAME = "surveyUnitCompositeName";


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
                false,
                null,
                null,
                String.format("%s-%s-%s",source.getId().toLowerCase(), survey.getYear(), campaign.getPeriodCollect()),
                campaign.getDataCollectionTarget(),
                source.getId().toLowerCase(),
                survey.getYear(),
                Optional.ofNullable(campaign.getPeriodCollect())
                        .map(PeriodEnum::name)
                        .orElse(""),
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
        String questioningId = context.questioningId().toString();
        String encodedLabel = "";

        if (context.isBusiness()) {
            String surveyUnitLabelDetails = buildSurveyUnitCompositeName(
                    context.surveyUnitLabel(),
                    context.surveyUnitIdentificationName(),
                    context.surveyUnitId());
            encodedLabel = Base64.getUrlEncoder().withoutPadding().encodeToString(surveyUnitLabelDetails.getBytes(StandardCharsets.UTF_8));
        }

        return switch (StringUtils.defaultString(role).toLowerCase()) {
            case UserRoles.REVIEWER -> {
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromUriString(baseUrl)
                        .pathSegment("v3", "review", "interrogations", questioningId);

                if (context.isBusiness()) {
                    builder.queryParam(SURVEY_UNIT_LABEL, encodedLabel);
                }

                yield builder.build().toUriString();
            }
            case UserRoles.INTERVIEWER -> {
                String urlAssistance = String.format("/mes-enquetes/%s/contacter-assistance/auth?interrogationId=%s&surveyUnitId=%s&contactId=%s",
                        context.sourceId(), context.questioningId(), context.surveyUnitId(), context.contactId());
                String encodedAssistance = Base64.getUrlEncoder().withoutPadding().encodeToString(urlAssistance.getBytes());
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromUriString(baseUrl)
                        .pathSegment("v3", "interrogations", questioningId);

                if (context.isBusiness()) {
                    builder.queryParam(SURVEY_UNIT_LABEL, encodedLabel);
                }

                yield builder
                        .queryParam(PATH_ASSISTANCE, encodedAssistance)
                        .toUriString();
            }
            default -> "";
        };
    }

    /**
     * Builds deposit proof based on the provided parameters
     * @param ctx context for building url
     * @return the deposit proof url for the associated questioning
     */
    public String buildDepositProofUrl(QuestioningUrlContext ctx) {
        String baseUrl = switch (ctx.dataCollection()) {
            case LUNATIC_NORMAL -> questionnaireApiUrl;
            case LUNATIC_SENSITIVE -> questionnaireApiSensitiveUrl;
            default -> null;
        };

        if (baseUrl == null) {
            return null;
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/api/interrogations/{questioningId}/deposit-proof");

        if (ctx.isBusiness()) {
            String surveyUnitLabelDetails = buildSurveyUnitCompositeName(
                    ctx.surveyUnitLabel(),
                    ctx.surveyUnitIdentificationName(),
                    ctx.surveyUnitId()
            );
            builder.queryParam(SURVEY_UNIT_COMPOSITE_NAME, surveyUnitLabelDetails);
        }

        return builder.buildAndExpand(ctx.questioningId()).toUriString();
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

    public String buildSurveyUnitCompositeName(String label, String identificationName, String surveyUnitId) {
        if (StringUtils.isBlank(label)) {
            return String.format("%s (%s)", identificationName, surveyUnitId);
        }
        return String.format("%s %s (%s)", StringUtils.capitalize(label), identificationName, surveyUnitId);
    }

}
