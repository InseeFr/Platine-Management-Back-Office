package fr.insee.survey.datacollectionmanagement.metadata.controller;


import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.util.JsonUtil;
import net.minidev.json.JSONObject;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CampaignControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    CampaignService campaignService;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getCampaignNotFound() throws Exception {
        String identifier = "CAMPAIGNNOTFOUND";
        this.mockMvc.perform(get(Constants.API_CAMPAIGNS_ID, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }


    @Test
    void putCampaignsErrorId() throws Exception {
        String identifier = "SOURCE12023T01";
        String otherIdentifier = "WRONG";
        Campaign campaign = initOpenedCampaign(identifier);
        String jsonCampaign = createJson(campaign, "SOURCE12023");
        String jsonError = JsonUtil.createJsonErrorBadRequest("id and idCampaign don't match");
        mockMvc.perform(put(Constants.API_CAMPAIGNS_ID, otherIdentifier).content(jsonCampaign)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(content().json(jsonError, false));

    }

    @Test
    void isCampaignOnGoing() throws Exception {
        String identifier = "OPENED";
        Campaign campaign = initOpenedCampaign(identifier);
        initCampaignAndPartitionings(identifier, campaign);

        this.mockMvc.perform(get(Constants.CAMPAIGNS_ID_ONGOING, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""
                        {"ongoing": true}
                        """, false));

    }

    @Test
    void isCampaignOnGoingClose() throws Exception {
        String identifier = "CLOSED";
        Campaign campaign = initClosedCampaign(identifier);
        initCampaignAndPartitionings(identifier, campaign);

        this.mockMvc.perform(get(Constants.CAMPAIGNS_ID_ONGOING, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""
                        {"ongoing": false}
                        """, false));

    }

    @Test
    void isCampaignOnGoingFutureCampaignFalse() throws Exception {
        String identifier = "FUTURE";
        Campaign campaign = initFutureCampaign(identifier);
        initCampaignAndPartitionings(identifier, campaign);


        this.mockMvc.perform(get(Constants.CAMPAIGNS_ID_ONGOING, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""
                        {"ongoing": false}
                        """, false));

    }

    @Test
    void isCampaignOnGoingEmptyCampaignFalse() throws Exception {
        String identifier = "EMPTY";
        Campaign campaign = initEmptyCampaign(identifier);
        String jsonCampaign = createJson(campaign, "SOURCE12023");

        mockMvc.perform(
                        put(Constants.API_CAMPAIGNS_ID, identifier).content(jsonCampaign)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonCampaign.toString(), false));


        this.mockMvc.perform(get(Constants.CAMPAIGNS_ID_ONGOING, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("""
                        {"ongoing": false}
                        """, false));

    }

    private void initCampaignAndPartitionings(String identifier, Campaign campaign) throws Exception {
        String jsonCampaign = createJson(campaign, "SOURCE12023");

        mockMvc.perform(
                        put(Constants.API_CAMPAIGNS_ID, identifier).content(jsonCampaign)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonCampaign.toString(), false));

        Partitioning part1 = campaign.getPartitionings().stream().toList().get(0);
        String jsonPart1 = createJsonPart(part1);

        mockMvc.perform(
                        put(Constants.API_PARTITIONINGS_ID, part1.getId()).content(jsonPart1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        Partitioning part2 = campaign.getPartitionings().stream().toList().get(1);
        String jsonPart2 = createJsonPart(part2);

        mockMvc.perform(
                        put(Constants.API_PARTITIONINGS_ID, part2.getId()).content(jsonPart2)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    private Campaign initOpenedCampaign(String identifier) {
        return initCampaign(identifier, DateUtil.parse("2000-01-01"), DateUtil.parse("2050-01-01"));
    }

    private Campaign initClosedCampaign(String identifier) {
        return initCampaign(identifier, DateUtil.parse("2010-01-01"), DateUtil.parse("2010-01-31"));
    }

    private Campaign initFutureCampaign(String identifier) {
        return initCampaign(identifier, DateUtil.parse("2050-01-01"), DateUtil.parse("2050-01-31"));
    }

    private Campaign initEmptyCampaign(String identifier) {
        Campaign empty = initCampaign(identifier, DateUtil.parse("2050-01-01"), DateUtil.parse("2050-01-31"));
        empty.setPartitionings(new HashSet<>());
        return empty;
    }


    private Campaign initCampaign(String identifier, Date openingDate, Date closingDate) {
        Campaign campaignMock = new Campaign();
        campaignMock.setYear(2023);
        campaignMock.setPeriod(PeriodEnum.A00);
        campaignMock.setId(identifier);
        campaignMock.setPartitionings(Set.of(
                initPartitioning(identifier, "01", openingDate, closingDate, campaignMock),
                initPartitioning(identifier, "012", openingDate, closingDate, campaignMock)
        ));
        campaignMock.setCampaignWording("Short wording about " + identifier);


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

    @Test
    @Transactional
    @Rollback
    void getCampaignOk() throws Exception {
        String identifier = "SOURCE12023T01";

        assertDoesNotThrow(() -> campaignService.findById(identifier));
        Campaign campaign = campaignService.findById(identifier);
        String json = createJson(campaign, "SOURCE12023");
        this.mockMvc.perform(get(Constants.API_CAMPAIGNS_ID, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }


}