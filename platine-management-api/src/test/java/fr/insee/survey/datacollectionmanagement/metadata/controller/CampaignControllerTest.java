package fr.insee.survey.datacollectionmanagement.metadata.controller;


import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.QuestioningCsvDto;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import net.minidev.json.JSONObject;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CampaignControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    CampaignService campaignService;

    @Autowired
    QuestioningService questioningService;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getCampaignNotFound() throws Exception {
        String campaignId = "CAMPAIGNNOTFOUND";
        this.mockMvc.perform(get(UrlConstants.API_CAMPAIGNS_ID, campaignId)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }


    @Test
    void putCampaignsErrorId() throws Exception {
        String campaignId = "SOURCE12023T01";
        String othercampaignId = "WRONG";
        String surveyId = "SOURCE12023";
        Campaign campaign = initOpenedCampaign(campaignId);
        String jsonCampaign = createJson(campaign, surveyId);
        mockMvc.perform(put(UrlConstants.API_CAMPAIGNS_ID, othercampaignId).content(jsonCampaign)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("id and idCampaign don't match"));
    }

    @Test
    void putCampaignsErrorDataCollectionTarget() throws Exception {
        String campaignId = "SOURCE12023T1";
        String surveyId = "SOURCE12023";

        Campaign campaign = initOpenedCampaign(campaignId);
        String jsonCampaign = createJson(campaign, surveyId, false, "WRONG_TARGET");
        mockMvc.perform(put(UrlConstants.API_CAMPAIGNS_ID, campaignId).content(jsonCampaign)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("DataCollectionTarget missing or not recognized. Only LUNATIC_NORMAL, LUNATIC_SENSITIVE, XFORM1, XFORM2, FILE_UPlOAD are valid"));
    }

    @Test
    void putCampaignsLunaticNormalOk() throws Exception {
        String campaignId = "SOURCE12023X00";
        String surveyId = "SOURCE12023";
        Campaign campaign = initOpenedCampaign(campaignId);
        String jsonCampaign = createJson(campaign, surveyId, false, DataCollectionEnum.LUNATIC_NORMAL.name());
        mockMvc.perform(put(UrlConstants.API_CAMPAIGNS_ID, campaignId).content(jsonCampaign)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void putCampaignsLunaticSensitivityOk() throws Exception {
        String campaignId = "SOURCE12023X01";
        String surveyId = "SOURCE12023";
        Campaign campaign = initOpenedCampaign(campaignId);
        String jsonCampaign = createJson(campaign, surveyId, true, DataCollectionEnum.LUNATIC_SENSITIVE.name());
        mockMvc.perform(put(UrlConstants.API_CAMPAIGNS_ID, campaignId).content(jsonCampaign)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @Test
    void isCampaignOnGoing() throws Exception {
        String campaignId = "OPENED";
        Campaign campaign = initOpenedCampaign(campaignId);
        initCampaignAndPartitionings(campaignId, campaign);

        this.mockMvc.perform(get(UrlConstants.CAMPAIGNS_ID_ONGOING, campaignId)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.ongoing").value(true));

    }

    @Test
    void isCampaignOnGoingClose() throws Exception {
        String campaignId = "CLOSED";
        Campaign campaign = initClosedCampaign(campaignId);
        initCampaignAndPartitionings(campaignId, campaign);

        this.mockMvc.perform(get(UrlConstants.CAMPAIGNS_ID_ONGOING, campaignId)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.ongoing").value(false));


    }

    @Test
    void isCampaignOnGoingFutureCampaignFalse() throws Exception {
        String campaignId = "FUTURE";
        Campaign campaign = initFutureCampaign(campaignId);
        initCampaignAndPartitionings(campaignId, campaign);

        this.mockMvc.perform(get(UrlConstants.CAMPAIGNS_ID_ONGOING, campaignId)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.ongoing").value(false));
    }

    @Test
    void isCampaignOnGoingEmptyCampaignFalse() throws Exception {
        String campaignId = "EMPTY";
        String surveyId = "SOURCE12023";
        Campaign campaign = initEmptyCampaign(campaignId);
        String jsonCampaign = createJson(campaign, surveyId);

        mockMvc.perform(
                        put(UrlConstants.API_CAMPAIGNS_ID, campaignId).content(jsonCampaign)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaignWording").value(campaign.getCampaignWording()))
                .andExpect(jsonPath("$.surveyId").value(surveyId))
                .andExpect(jsonPath("$.year").value(campaign.getYear()));

        this.mockMvc.perform(get(UrlConstants.CAMPAIGNS_ID_ONGOING, campaignId)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.ongoing").value(false));

    }

    @Test
    void deleteCampaign() throws Exception {
        String campaignId = "CLOSED";
        Campaign campaign = initClosedCampaign(campaignId);
        initCampaignAndPartitionings(campaignId, campaign);

        this.mockMvc.perform(delete(UrlConstants.API_CAMPAIGNS_ID, campaignId)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getCampaigns() throws Exception {
        String campaignId = "OPENED";

        this.mockMvc.perform(get(UrlConstants.API_CAMPAIGNS, campaignId)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(16)));
    }

    private void initCampaignAndPartitionings(String campaignId, Campaign campaign) throws Exception {
        String surveyId = "SOURCE12023";
        String jsonCampaign = createJson(campaign, surveyId);

        mockMvc.perform(
                        put(UrlConstants.API_CAMPAIGNS_ID, campaignId).content(jsonCampaign)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaignWording").value(campaign.getCampaignWording()))
                .andExpect(jsonPath("$.surveyId").value(surveyId))
                .andExpect(jsonPath("$.year").value(campaign.getYear()));


        List<Partitioning> partitions = campaign.getPartitionings().stream().toList();
        Partitioning part1 = partitions.getFirst();
        String jsonPart1 = createJsonPart(part1);

        mockMvc.perform(
                        put(UrlConstants.API_PARTITIONINGS_ID, part1.getId()).content(jsonPart1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        Partitioning part2 = partitions.get(1);
        String jsonPart2 = createJsonPart(part2);

        mockMvc.perform(
                        put(UrlConstants.API_PARTITIONINGS_ID, part2.getId()).content(jsonPart2)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    private Campaign initOpenedCampaign(String campaignId) {
        return initCampaign(campaignId, DateUtil.parse("2000-01-01"), DateUtil.parse("2050-01-01"));
    }

    private Campaign initClosedCampaign(String campaignId) {
        return initCampaign(campaignId, DateUtil.parse("2010-01-01"), DateUtil.parse("2010-01-31"));
    }

    private Campaign initFutureCampaign(String campaignId) {
        return initCampaign(campaignId, DateUtil.parse("2050-01-01"), DateUtil.parse("2050-01-31"));
    }

    private Campaign initEmptyCampaign(String campaignId) {
        Campaign empty = initCampaign(campaignId, DateUtil.parse("2050-01-01"), DateUtil.parse("2050-01-31"));
        empty.setPartitionings(new HashSet<>());
        return empty;
    }

    private Campaign initCampaign(String campaignId, Date openingDate, Date closingDate) {
        Campaign campaignMock = new Campaign();
        campaignMock.setYear(2023);
        campaignMock.setPeriod(PeriodEnum.A00);
        campaignMock.setId(campaignId);
        campaignMock.setPartitionings(Set.of(
                initPartitioning(campaignId, "01", openingDate, closingDate, campaignMock),
                initPartitioning(campaignId, "012", openingDate, closingDate, campaignMock)
        ));
        campaignMock.setCampaignWording("Short wording about " + campaignId);
        return campaignMock;
    }

    private Partitioning initPartitioning(String campaignId, String s, Date openingDate, Date closingDate, Campaign campaignMock) {
        Partitioning part = new Partitioning();
        part.setId(campaignId + s);
        part.setOpeningDate(openingDate);
        part.setClosingDate(closingDate);
        part.setCampaign(campaignMock);
        return part;
    }

    private String createJsonPart(Partitioning part) {
        JSONObject jo = new JSONObject();
        jo.put("id", part.getId());
        jo.put("campaignId", part.getCampaign().getId());
        jo.put("openingDate", part.getOpeningDate().toInstant().toString());
        jo.put("closingDate", part.getClosingDate().toInstant().toString());
        jo.put("returnDate", part.getClosingDate().toInstant().toString());
        jo.put("label", "label");
        return jo.toString();
    }

    private String createJson(Campaign campaign, String idSurvey) {
        JSONObject jo = new JSONObject();
        jo.put("id", campaign.getId());
        jo.put("year", campaign.getYear());
        jo.put("surveyId", idSurvey);
        jo.put("campaignWording", campaign.getCampaignWording());
        jo.put("period", campaign.getPeriod().toString());
        return jo.toString();
    }

    private String createJson(Campaign campaign, String idSurvey, boolean sensitivity, String dataCollectionTarget) {
        JSONObject jo = new JSONObject();
        jo.put("id", campaign.getId());
        jo.put("year", campaign.getYear());
        jo.put("surveyId", idSurvey);
        jo.put("campaignWording", campaign.getCampaignWording());
        jo.put("period", campaign.getPeriod().toString());
        jo.put("sensitivity", sensitivity);
        jo.put("dataCollectionTarget", dataCollectionTarget);
        return jo.toString();
    }


    @Test
    @Transactional
    @Rollback
    void getCampaignOk() throws Exception {
        String campaignId = "SOURCE12023T01";

        assertDoesNotThrow(() -> campaignService.findById(campaignId));
        Campaign campaign = campaignService.findById(campaignId);
        this.mockMvc.perform(get(UrlConstants.API_CAMPAIGNS_ID, campaignId)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignWording").value(campaign.getCampaignWording()))
                .andExpect(jsonPath("$.surveyId").value(campaign.getSurvey().getId()))
                .andExpect(jsonPath("$.year").value(campaign.getYear()));
    }

    @Test
    void should_return_ongoing_campaigns() throws Exception {
        // Given
        Campaign campaign1 = initOpenedCampaign("CAMP1");
        initCampaignAndPartitionings("CAMP1", campaign1);
        Campaign campaign2 = initOpenedCampaign("CAMP2");
        initCampaignAndPartitionings("CAMP2", campaign2);
        Campaign campaign3 = initFutureCampaign("CAMP3");
        initCampaignAndPartitionings("CAMP3", campaign3);

        // when / then
        mockMvc.perform(get(UrlConstants.API_CAMPAIGNS_COMMONS_ONGOING))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("CAMP1"))
                .andExpect(jsonPath("$[0].dataCollectionTarget").value("LUNATIC_NORMAL"))
                .andExpect(jsonPath("$[0].sensitivity").value(false))
                .andExpect(jsonPath("$[0].collectMode").value("WEB"))
                .andExpect(jsonPath("$[1].id").value("CAMP2"))
                .andExpect(jsonPath("$[1].dataCollectionTarget").value("LUNATIC_NORMAL"))
                .andExpect(jsonPath("$[1].sensitivity").value(false))
                .andExpect(jsonPath("$[1].collectMode").value("WEB"));
    }

    @Test
    void should_return_ongoing_campaigns_by_id() throws Exception {
        // Given
        Campaign campaign1 = initOpenedCampaign("CAMP1");
        initCampaignAndPartitionings("CAMP1", campaign1);

        String campaignId = "CAMP1";

        // when / then
        mockMvc.perform(get(UrlConstants.API_CAMPAIGNS_COMMONS_ID, campaignId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("CAMP1"))
                .andExpect(jsonPath("dataCollectionTarget").value("LUNATIC_NORMAL"))
                .andExpect(jsonPath("sensitivity").value(false))
                .andExpect(jsonPath("collectMode").value("WEB"));
    }

    @Test
    void should_return_campaigns_by_id() throws Exception {
        // Given
        Campaign campaign3 = initFutureCampaign("CAMP3");
        initCampaignAndPartitionings("CAMP3", campaign3);


        String campaignId = "CAMP3";

        // when / then
        mockMvc.perform(get(UrlConstants.API_CAMPAIGNS_COMMONS_ID, campaignId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("CAMP3"))
                .andExpect(jsonPath("dataCollectionTarget").value("LUNATIC_NORMAL"))
                .andExpect(jsonPath("sensitivity").value(false))
                .andExpect(jsonPath("collectMode").value("WEB"));
    }

    @Test
    void should_return_campaigns_by_id_is_null() throws Exception {
        // Given
        String campaignId = "NOT_FOUND";

        // when / then
        mockMvc.perform(get(UrlConstants.API_CAMPAIGNS_COMMONS_ID, campaignId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadQuestioningsCsv_shouldReturnValidCsvHouseHold_whenDataExists() throws Exception {
        String campaignId = "SOURCE22023T01"; // household source

        List<QuestioningCsvDto> data = questioningService.getQuestioningsByCampaignIdForCsv(campaignId);

        mockMvc.perform(get(UrlConstants.API_CAMPAIGN_ID_QUESTIONINGS_CSV, campaignId))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_TYPE,
                        "text/csv;charset=UTF-8"))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"SOURCE22023T01.csv\""
                ))
                .andExpect(result -> verifyCsvContent(result, data, false));
    }

    @Test
    void downloadQuestioningsCsv_shouldReturnValidCsvBusiness_whenDataExists() throws Exception {
        String campaignId = "SOURCE12023T01"; // business source

        List<QuestioningCsvDto> data = questioningService.getQuestioningsByCampaignIdForCsv(campaignId);

        mockMvc.perform(get(UrlConstants.API_CAMPAIGN_ID_QUESTIONINGS_CSV, campaignId))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_TYPE,
                        "text/csv;charset=UTF-8"))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"SOURCE12023T01.csv\""
                ))
                .andExpect(result -> verifyCsvContent(result, data, true));
    }

    @Test
    void downloadQuestioningsCsv_shouldReturnEmptyCsv_whenNoData() throws Exception {
        String campaignId = "SOURCE12023T01";

        List<QuestioningCsvDto> data = questioningService.getQuestioningsByCampaignIdForCsv(campaignId);

        for (QuestioningCsvDto questioningCsvDto : data) {
            questioningService.deleteQuestioning(questioningCsvDto.interrogationId());
        }

        mockMvc.perform(get(UrlConstants.API_CAMPAIGN_ID_QUESTIONINGS_CSV, campaignId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    String[] lines = content.split("\n");
                    assertEquals(1, lines.length, "Should only contain header");
                    assertTrue(lines[0].contains("partitioningId"), "Missing header");
                });
    }

    @Test
    void downloadQuestioningsCsv_shouldReturnNotFoundWhenUnknownCampaign() throws Exception {
        String campaignId = "NOT_FOUND";

        mockMvc.perform(get(UrlConstants.API_CAMPAIGN_ID_QUESTIONINGS_CSV, campaignId))
                .andExpect(status().isNotFound());
    }

    private void verifyCsvContent(MvcResult result, List<QuestioningCsvDto> expectedData, boolean isBusiness) throws Exception {
        String csvContent = result.getResponse().getContentAsString();
        String[] lines = csvContent.split("\n");

        assertEquals(expectedData.size() + 1, lines.length, "Wrong number of lines");

        assertThat(lines[0]).contains(
                "partitioningId", "surveyUnitId", "interrogationId",
                "highestEventType", "highestEventDate"
        );

        if (isBusiness) {
            assertThat(lines[0]).contains("isOnProbation");
        }


        for (int i = 0; i < expectedData.size(); i++) {
            QuestioningCsvDto dto = expectedData.get(i);
            String line = lines[i + 1];

            assertThat(line)
                    .contains(dto.partitioningId())
                    .contains(dto.surveyUnitId())
                    .contains(dto.interrogationId().toString())
                    .contains(dto.highestEventType() != null ? dto.highestEventType().name() : "")
                    .contains(dto.highestEventDate() != null ? dto.highestEventDate().toString(): "");
            if (isBusiness) {
                assertThat(line).contains(dto.isOnProbation()+"");
            }
        }
    }

    @Test
    void should_return_ongoing_campaigns_with_filter_all() throws Exception {
        // Given
        Campaign campaign1 = initOpenedCampaign("CAMP1");
        initCampaignAndPartitionings("CAMP1", campaign1);
        Campaign campaign2 = initOpenedCampaign("CAMP2");
        initCampaignAndPartitionings("CAMP2", campaign2);
        Campaign campaign3 = initFutureCampaign("CAMP3");
        initCampaignAndPartitionings("CAMP3", campaign3);
        
        // When / Then
        mockMvc.perform(get(UrlConstants.API_CAMPAIGNS_COMMONS_ONGOING)
                        .param("walletFilter", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("CAMP1"))
                .andExpect(jsonPath("$[1].id").value("CAMP2"));
    }

}