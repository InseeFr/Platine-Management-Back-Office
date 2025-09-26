package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SourceTypeEnum;
import fr.insee.survey.datacollectionmanagement.query.domain.QuestioningInformations;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningInformationsDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningServiceStub;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuestioningInformationsServiceImplTest {


    @Test
    @DisplayName("Should format civility for male with full name")
    void testGetFormattedCivility_Male() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility("Male", "John", "Doe");
        assertEquals("M. John Doe", result);
    }

    @Test
    @DisplayName("Should format civility for female with full name")
    void testGetFormattedCivility_Female() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility("Female", "Jane", "Doe");
        assertEquals("Mme Jane Doe", result);
    }

    @Test
    @DisplayName("Should format civility with no gender")
    void testGetFormattedCivility_NoGender() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility(null, "Alex", "Doe");
        assertEquals("Alex Doe", result);
    }

    @Test
    @DisplayName("Should format civility with no first name")
    void testGetFormattedCivility_NoFirstName() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility("Male", null, "Doe");
        assertEquals("M. Doe", result);
    }

    @Test
    @DisplayName("Should format civility with no last name")
    void testGetFormattedCivility_NoLastName() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility("Female", "Jane", null);
        assertEquals("Mme Jane", result);
    }

    @Test
    @DisplayName("Should format civility with no gender and first name")
    void testGetFormattedCivility_NoGenderAndFirstName() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility(null, null, "Doe");
        assertEquals("Doe", result);
    }

    @Test
    @DisplayName("Should format civility with no gender and last name")
    void testGetFormattedCivility_NoGenderAndLastName() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility(null, "Jane", null);
        assertEquals("Jane", result);
    }

    @Test
    @DisplayName("Should format civility with no names")
    void testGetFormattedCivility_NoNames() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility("Male", null, null);
        assertEquals("M.", result);
    }

    @Test
    @DisplayName("Should format civility with empty strings")
    void testGetFormattedCivility_EmptyStrings() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility("Female", "", "");
        assertEquals("Mme", result);
    }

    @Test
    @DisplayName("Should format civility with trimmed inputs")
    void testGetFormattedCivility_TrimmedInputs() {
        String result = QuestioningInformationsServiceImpl.getFormattedCivility("Male", " John ", " Doe ");
        assertEquals("M. John Doe", result);
    }


    @Test
    @DisplayName("Should return secondary phone when primary phone is null")
    void testGetFormattedPhone_NullPrimaryPhone() {
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, null, null);
        String result = service.getFormattedPhone(null, "0987654321");
        assertEquals("0987654321", result);
    }

    @Test
    @DisplayName("Should return secondary phone when primary phone is blank")
    void testGetFormattedPhone_BlankPrimaryPhone() {
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, null, null);
        String result = service.getFormattedPhone("  ", "0987654321");
        assertEquals("0987654321", result);
    }

    @Test
    @DisplayName("Should return null when both phones are null")
    void testGetFormattedPhone_BothPhonesNull() {
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, null, null);
        String result = service.getFormattedPhone(null, null);
        assertEquals(null, result);
    }

    @Test
    @DisplayName("Should return null when both phones are blank")
    void testGetFormattedPhone_BothPhonesBlank() {
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, null, null);
        String result = service.getFormattedPhone("  ", "  ");
        assertEquals(null, result);
    }

    @Test
    @DisplayName("Should return primary phone when both phones are non-null and non-blank")
    void testGetFormattedPhone_BothPhonesNonBlank() {
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, null, null);
        String result = service.getFormattedPhone("1234567890", "0987654321");
        assertEquals("1234567890", result);
    }

    @Test
    @DisplayName("Should return primary phone when primary phone is not blank and secondary phone is blank")
    void testGetFormattedPhone_PrimaryNonBlankSecondaryBlank() {
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, null, null);
        String result = service.getFormattedPhone("1234567890", " ");
        assertEquals("1234567890", result);
    }

    @Test
    @DisplayName("Should return secondary phone when primary phone is blank and secondary phone is not blank")
    void testGetFormattedPhone_PrimaryBlankSecondaryNonBlank() {
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, null, null);
        String result = service.getFormattedPhone(" ", "0987654321");
        assertEquals("0987654321", result);
    }

    QuestioningInformations initInfos(boolean isBusiness)
    {
        UUID uuid = UUID.randomUUID();
        QuestioningInformations infos = new QuestioningInformations();
        infos.setReturnDate("2025-05-10");
        infos.setLogo("logo.png");
        infos.setSourceId("source123");
        infos.setQuestioningId(uuid);
        infos.setIdentificationCode("id789");
        infos.setIdentifier("cont123");
        infos.setGender("Male");
        infos.setFirstName("John");
        infos.setLastName("Doe");
        infos.setEmail("john.doe@example.com");
        infos.setPhone("123456789");
        infos.setPhone2("987654321");
        infos.setUsualCompanyName("Doe Inc.");
        infos.setLabel("Survey Label");
        infos.setIdSu("su123");
        infos.setIdentificationName("Name123");
        infos.setStreetName("Main St");
        infos.setStreetNumber("123");
        infos.setSpecialDistribution("Special");
        infos.setSourceType(isBusiness ? SourceTypeEnum.BUSINESS.toString() : SourceTypeEnum.HOUSEHOLD.toString());
        return infos;
    }

    @Test
    void testMapQuestioningInformationsDto_shouldMapCorrectly() {
        // Given
        QuestioningInformations infos = initInfos(true);

        // When
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, null, null);
        QuestioningInformationsDto result = service.mapQuestioningInformationsDto(infos);
        // Then
        assertNotNull(result);
        assertEquals("2025-05-10", result.getReturnDate());
        assertEquals("logo.png", result.getLogo());
        assertEquals("/mes-enquetes", result.getUrlLogout());

        String expectedUrlAssistance = String.format("/assistance/faq-entreprise/contact?interrogationId=%s&suId=%s&sourceId=%s",
                 infos.getQuestioningId().toString(), infos.getIdentificationCode(), infos.getSourceId().toLowerCase());
        assertEquals(URLEncoder.encode(expectedUrlAssistance, StandardCharsets.UTF_8), result.getUrlAssistance());

        assertNotNull(result.getContactInformationsDto());
        assertEquals("M. John Doe", result.getContactInformationsDto().getIdentity());
        assertEquals("john.doe@example.com", result.getContactInformationsDto().getEmail());
        assertEquals("123456789", result.getContactInformationsDto().getPhoneNumber());
        assertEquals("Doe Inc.", result.getContactInformationsDto().getUsualCompanyName());
        assertEquals("Main St", result.getContactInformationsDto().getAddressInformationsDto().getStreetName());
        assertEquals("123", result.getContactInformationsDto().getAddressInformationsDto().getStreetNumber());
        assertEquals("Special", result.getContactInformationsDto().getAddressInformationsDto().getSpecialDistribution());


        assertNotNull(result.getSurveyUnitInformationsDto());
        assertEquals("Survey Label", result.getSurveyUnitInformationsDto().getLabel());
        assertEquals("su123", result.getSurveyUnitInformationsDto().getSurveyUnitId());
        assertEquals("Name123", result.getSurveyUnitInformationsDto().getIdentificationName());
    }

    @Test
    void testMapQuestioningInformationsDto_houseHoldRedirectionForm() {
        // Given
        QuestioningInformations infos = initInfos(false);

        // When
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, null, null);
        QuestioningInformationsDto result = service.mapQuestioningInformationsDto(infos);


        String expectedUrlAssistance = String.format("/assistance/faq-particulier/contact?interrogationId=%s&suId=%s&sourceId=%s",
                infos.getQuestioningId().toString(), infos.getIdSu(), infos.getSourceId().toLowerCase());

        assertEquals(URLEncoder.encode(expectedUrlAssistance, StandardCharsets.UTF_8), result.getUrlAssistance());
    }


    @Test
    void testFindQuestioningInformationsDtoReviewer_noPartsFound() {
        // Arrange
        CampaignServiceStub campaignService = new CampaignServiceStub();
        QuestioningServiceStub questioningService = new QuestioningServiceStub();
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, campaignService, questioningService);

        Assertions.assertThatThrownBy(()->service.findQuestioningInformationsDtoReviewer("camp1", "su1")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindQuestioningInformationsDtoInterviewer_noPartsFound() {
        // Arrange
        CampaignServiceStub campaignService = new CampaignServiceStub();
        QuestioningServiceStub questioningService = new QuestioningServiceStub();
        QuestioningInformationsServiceImpl service = new QuestioningInformationsServiceImpl(null, campaignService, questioningService);

        Assertions.assertThatThrownBy(()->service.findQuestioningInformationsDtoInterviewer("camp1", "su1", "cont123")).isInstanceOf(NotFoundException.class);
    }
}