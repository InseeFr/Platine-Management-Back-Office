package fr.insee.survey.datacollectionmanagement.questioning.validation;

import fr.insee.survey.datacollectionmanagement.questioning.enums.ValidationQuestioningPriorityErrorType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ValidationQuestioningPriorityErrorTest {

    @Test
    void toString_shouldFormatMessage_forInterrogationIdNull_singleLine() {
        // given
        ValidationQuestioningPriorityError error =
                new ValidationQuestioningPriorityError(
                        ValidationQuestioningPriorityErrorType.NULL_INTERROGATION_ID,
                        3
                );

        // when
        String result = error.toString();

        // then
        assertEquals("Record [3]: Interrogation id cannot be null", result);
    }

    @Test
    void toString_shouldFormatMessage_forDuplicateInterrogationId() {
        // given
        UUID interrogationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        ValidationQuestioningPriorityError error =
                new ValidationQuestioningPriorityError(
                        ValidationQuestioningPriorityErrorType.DUPLICATE_INTERROGATION_ID,
                        List.of(4, 5),
                        interrogationId
                );

        // when
        String result = error.toString();

        // then
        assertEquals(
                "Record [4, 5]: Duplicate interrogation id 123e4567-e89b-12d3-a456-426614174000 is not allowed",
                result
        );
    }

    @Test
    void toString_shouldFormatMessage_forUnknownInterrogationId() {
        // given
        UUID interrogationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        ValidationQuestioningPriorityError error =
                new ValidationQuestioningPriorityError(
                        ValidationQuestioningPriorityErrorType.UNKNOWN_INTERROGATION_ID,
                        10,
                        interrogationId
                );

        // when
        String result = error.toString();

        // then
        assertEquals(
                "Record [10]: Unknown interrogation id 123e4567-e89b-12d3-a456-426614174000",
                result
        );
    }
}