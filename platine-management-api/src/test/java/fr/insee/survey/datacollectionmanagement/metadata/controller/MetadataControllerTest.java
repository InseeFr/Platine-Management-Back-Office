package fr.insee.survey.datacollectionmanagement.metadata.controller;


import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MetadataControllerTest {


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
        String campaignId = "CAMPAIGNNOTFOUND";
        this.mockMvc.perform(get(UrlConstants.API_METADATA_BUSINESS, campaignId)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void getCampaignOk() throws Exception {
        String campaignId = "SOURCE12023T01";

        assertDoesNotThrow(() -> campaignService.getById(campaignId));
        Campaign campaign = campaignService.getById(campaignId);
        this.mockMvc.perform(get(UrlConstants.API_METADATA_BUSINESS, campaignId)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/InformationsCollecte/ServiceProducteur/Libelle")
                        .string(Optional.ofNullable(campaign.getSurvey().getSource().getOwner().getLabel()).orElse("")))
                .andExpect(xpath("/InformationsCollecte/ServiceProducteur/MinistereTutelle")
                        .string(Optional.ofNullable(campaign.getSurvey().getSource().getOwner().getMinistry()).orElse("")))
                .andExpect(xpath("/InformationsCollecte/Enquete/AnneeCollecte")
                        .string(Optional.ofNullable(String.valueOf(campaign.getSurvey().getYear())).orElse("")))
                .andExpect(xpath("/InformationsCollecte/Enquete/CaractereObligatoire")
                        .string("non"))
                .andExpect(xpath("/InformationsCollecte/Enquete/ObjectifsCourts")
                        .string(Optional.ofNullable(campaign.getSurvey().getShortObjectives()).orElse("")))
                .andExpect(xpath("/InformationsCollecte/Enquete/URLDiffusion")
                        .string(Optional.ofNullable(campaign.getSurvey().getDiffusionUrl()).orElse("")))
                .andExpect(xpath("/InformationsCollecte/Enquete/URLNotice")
                        .string(Optional.ofNullable(campaign.getSurvey().getNoticeUrl()).orElse("")))
                .andExpect(xpath("/InformationsCollecte/Enquete/URLSpecimen")
                        .string(Optional.ofNullable(campaign.getSurvey().getSpecimenUrl()).orElse("")))

                .andExpect(xpath("/InformationsCollecte/Campagne/Libelle")
                        .string(Optional.ofNullable(campaign.getCampaignWording()).orElse("")));
    }

}