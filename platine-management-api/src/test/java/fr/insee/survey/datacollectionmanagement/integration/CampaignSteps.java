package fr.insee.survey.datacollectionmanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignOngoingDto;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.impl.CampaignServiceImpl;
import fr.insee.survey.datacollectionmanagement.user.enums.WalletFilterEnum;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@RequiredArgsConstructor
public class CampaignSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final CampaignRepository campaignRepository;

    @Autowired
    CampaignServiceImpl campaignService;

    private String role;

    List<CampaignOngoingDto> listCampaignOngoingDto;

    @Given("the following campaign exist:")
    public void the_following_campaign_exist(io.cucumber.datatable.DataTable dataTable) {
        // Convert DataTable to a List of Maps
        List<Map<String, String>> campaigns = dataTable.asMaps(String.class, String.class);

        // Process each campaign entry (e.g., storing them in a mock database or calling your service)
        for (Map<String, String> campaign : campaigns) {
            String idCampaign = campaign.get("IdCampaign");
            Campaign campaignObject = new Campaign();
            campaignObject.setId(idCampaign);
            //campaignObject.setId("idCampaign");
            // Example: Print the campaign ID
            log.info("Campaign ID: " + idCampaign);

            // You could also use mockMvc to make requests to your application to create these campaigns
            // For example:
            // mockMvc.perform(post("/campaigns")
            //         .content(objectMapper.writeValueAsString(new CampaignRequest(idCampaign)))
            //         .contentType(MediaType.APPLICATION_JSON))
            //         .andExpect(s
            campaignRepository.save(campaignObject);


        }
        //throw new io.cucumber.java.PendingException();
    }

    @Given("I am a campaign manager")
    public void iAmACampaignManager() {
        role = AuthorityRoleEnum.INTERNAL_USER.name();
        SecurityContextHolder.getContext()
                .setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("USER", AuthorityRoleEnum.valueOf(role)));
    }

    @When("I type {string} in the searching campaign area by name")
    public void iTypeInTheSearchingCampaignAreaByName(String campaignName) throws Exception {
        mockMvc.perform(get(UrlConstants.API_CAMPAIGNS + "/C1"))
                .andExpect(status().isOk());
    }

    @Then("I found the following campaign")
    public void iFoundTheFollowingCampaign(io.cucumber.datatable.DataTable dataTable) {
    }


    @When("I search all opening campaigns for user {string}")
    public void iSearchAllOpeningCampaignsForUser(String userId) {
         listCampaignOngoingDto = campaignService.getCampaignOngoingDtos(userId, WalletFilterEnum.ALL);
    }

    @When("I search campaigns by wallet for user {string}")
    public void iSearchCampaignsByWalletForUser(String userId) {
        listCampaignOngoingDto = campaignService.getCampaignOngoingDtos(userId, WalletFilterEnum.MY_WALLET);
    }

    @When("I search campaigns by groups for user {string}")
    public void iSearchCampaignsByGroupsForUser(String userId) {
        listCampaignOngoingDto = campaignService.getCampaignOngoingDtos(userId, WalletFilterEnum.GROUPS);
    }

    @Then("the result should contain the following campaigns")
    public void theResultShouldContainTheFollowingCampaigns(List<String> expectedCampaignIds) {
        assertThat(listCampaignOngoingDto).hasSize(expectedCampaignIds.size());
        List<String> campaignIds = listCampaignOngoingDto.stream().map(CampaignOngoingDto::getId).toList();
        assertThat(campaignIds).containsExactlyInAnyOrderElementsOf(expectedCampaignIds);
    }


}

