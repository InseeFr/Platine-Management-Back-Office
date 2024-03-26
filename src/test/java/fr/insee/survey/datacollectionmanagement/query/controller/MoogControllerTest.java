package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.query.service.MoogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class MoogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MoogService moogService;

    @Test
    public void getMoogReadOnlyUrl() throws Exception {
        String idCampaign= "SOURCE12024T01";
        String surveyUnitId= "100000000";
        this.mockMvc.perform(get(Constants.MOOG_API_READONLY_URL, idCampaign,surveyUnitId)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("http://localhost:8081/readonly/questionnaire/m0/unite-enquetee/100000000"));
    }


    @Test
    public void getMoogReadOnlyUrlCampaignNotFound() throws Exception {
        String idCampaign= "SOURCE12023T00";
        String surveyUnitId= "100000000";
        this.mockMvc.perform(get(Constants.MOOG_API_READONLY_URL, idCampaign,surveyUnitId)).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void getMoogReadOnlyUrlQuestioningNotFound() throws Exception {
        String idCampaign= "SOURCE12023T01";
        String surveyUnitId= "10";
        this.mockMvc.perform(get(Constants.MOOG_API_READONLY_URL, idCampaign,surveyUnitId)).andDo(print()).andExpect(status().isNotFound());
    }
}
