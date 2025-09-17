package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSourceId;
import fr.insee.survey.datacollectionmanagement.contact.dto.BusinessContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.BusinessContactsDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessContactServiceImplTest {
    private static final String ID_CAMPAIGN1 = "ID_CAMPAIGN1";
    private static final String ID_SOURCE1 = "ID_SOURCE1";
    private static final String ID_SU1 = "ID_SU1";
    private static final String ID_CONTACT1 = "ID_CONTACT1";

    @Mock
    CampaignService campaignService;
    @Mock
    ContactService contactService;
    @Mock
    ContactSourceService contactSourceService;
    @InjectMocks
    BusinessContactServiceImpl service;

    private Campaign createCampaignWithSource(String campaignId, String sourceId) {
        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
        Survey survey = new Survey();
        Source source = new Source();
        source.setId(sourceId);
        survey.setSource(source);
        campaign.setSurvey(survey);
        return campaign;
    }

    private ContactSource createContactSource(String sourceId, String suId, String contactId, boolean isMain) {
        ContactSourceId id = new ContactSourceId();
        id.setSourceId(sourceId);
        id.setSurveyUnitId(suId);
        id.setContactId(contactId);

        ContactSource cs = new ContactSource();
        cs.setId(id);
        cs.setMain(isMain);
        return cs;
    }

    @Test
    @DisplayName("Should return one main contact when ContactSource exists")
    void findMainContactByCampaignAndSurveyUnit_withMainContact() {
        // Given
        Campaign campaign = createCampaignWithSource(ID_CAMPAIGN1, ID_SOURCE1);
        when(campaignService.findById(ID_CAMPAIGN1)).thenReturn(campaign);

        ContactSource contactSource = createContactSource(ID_SOURCE1, ID_SU1, ID_CONTACT1, true);
        when(contactSourceService.findMainContactSourceBySourceAndSurveyUnit(ID_SOURCE1, ID_SU1))
                .thenReturn(contactSource);

        Contact contact = new Contact();
        contact.setIdentifier(ID_CONTACT1);
        contact.setGender(GenderEnum.Male);
        when(contactService.findByIdentifier(ID_CONTACT1)).thenReturn(contact);

        // When
        BusinessContactsDto result = service.findMainContactByCampaignAndSurveyUnit(ID_CAMPAIGN1, ID_SU1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getBusinessContactDtoList())
                .extracting(BusinessContactDto::getIdeC)
                .containsExactly(ID_CONTACT1);
    }

    @Test
    @DisplayName("Should return empty result when no main contact exists")
    void findMainContactByCampaignAndSurveyUnit_withoutMainContact() {
        // Given
        Campaign campaign = createCampaignWithSource(ID_CAMPAIGN1, ID_SOURCE1);
        when(campaignService.findById(ID_CAMPAIGN1)).thenReturn(campaign);

        when(contactSourceService.findMainContactSourceBySourceAndSurveyUnit(ID_SOURCE1, ID_SU1))
                .thenReturn(null);

        // When
        BusinessContactsDto result = service.findMainContactByCampaignAndSurveyUnit(ID_CAMPAIGN1, ID_SU1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCount()).isEqualTo(0);
        assertThat(result.getBusinessContactDtoList()).isEmpty();
    }
}