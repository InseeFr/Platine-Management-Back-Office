package fr.insee.survey.datacollectionmanagement.questioning.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SurveyUnitParamEnumTest {

    @ParameterizedTest
    @ValueSource(strings = { "id", "ID", "code", "CODE", "name", "NAME" })
    void fromValue_shouldReturnEnum(String input) {
        assertNotNull(SurveyUnitParamEnum.fromValue(input));
    }

    @Test
    void fromValue_unknownValue_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> SurveyUnitParamEnum.fromValue("foo"));
    }
}
