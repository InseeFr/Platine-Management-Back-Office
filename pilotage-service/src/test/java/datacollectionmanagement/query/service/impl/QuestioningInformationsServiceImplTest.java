package datacollectionmanagement.query.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}