package fr.insee.survey.datacollectionmanagement.integration;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class CampaignSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    CampaignServiceImpl campaignService;

    @Autowired
    private TestSecurityContext testSecurityContext;

    List<CampaignOngoingDto> listCampaignOngoingDto;

    @Given("the following campaign exist:")
    public void the_following_campaign_exist(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> campaigns = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> campaign : campaigns) {
            String idCampaign = campaign.get("IdCampaign");
            Campaign campaignObject = new Campaign();
            campaignObject.setId(idCampaign);

            log.info("Campaign ID: {}", idCampaign);
            campaignRepository.save(campaignObject);
        }
    }

    @Given("I am a campaign manager")
    public void iAmACampaignManager() {
        testSecurityContext.setAuthentication(
                AuthenticationUserProvider.getAuthenticatedUser("USER", AuthorityRoleEnum.INTERNAL_USER)
        );
    }

    @When("I type {string} in the searching campaign area by name")
    public void iTypeInTheSearchingCampaignAreaByName(String campaignName) throws Exception {
        // NB: ton code ne se sert pas de campaignName et appelle /C1, je garde tel quel
        mockMvc.perform(get(UrlConstants.API_CAMPAIGNS + "/C1")
                        .with(authentication(testSecurityContext.getAuthentication())))
                .andExpect(status().isOk());
    }

    @Then("I found the following campaign")
    public void iFoundTheFollowingCampaign(io.cucumber.datatable.DataTable dataTable) {
        // TODO define this
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
