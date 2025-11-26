package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import fr.insee.survey.datacollectionmanagement.questioning.InterrogationPriorityInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.enums.ValidationQuestioningPriorityErrorType;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningPriorityService;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.validation.ValidationQuestioningPriorityError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestioningPriorityServiceImplTest {

    private QuestioningServiceStub questioningServiceStub;
    private QuestioningPriorityService questioningPriorityService;

    @BeforeEach
    void setUp() {
        questioningServiceStub = new QuestioningServiceStub();
        questioningPriorityService = new QuestioningPriorityServiceImpl(questioningServiceStub);
    }

    private InterrogationPriorityInputDto priority(UUID interrogationId) {
        return new InterrogationPriorityInputDto(interrogationId, 1l);
    }

    private Questioning questioning(UUID id) {
        Questioning q = new Questioning();
        q.setId(id);
        return q;
    }

    @Test
    void validatePriorityRules_shouldReturnEmpty_whenListIsNull() {
        List<ValidationQuestioningPriorityError> errors = questioningPriorityService.validatePriorityRules(null);

        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validatePriorityRules_shouldReturnEmpty_whenListIsEmpty() {
        List<ValidationQuestioningPriorityError> errors = questioningPriorityService.validatePriorityRules(List.of());

        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validatePriorityRules_shouldReturnOnlyNullIdErrors() {
        List<ValidationQuestioningPriorityError> errors =
                questioningPriorityService.validatePriorityRules(List.of(
                        priority(null)
                ));

        assertEquals(1, errors.size());

        ValidationQuestioningPriorityError error = errors.getFirst();
        assertEquals(ValidationQuestioningPriorityErrorType.NULL_INTERROGATION_ID, error.type());
        assertEquals(List.of(1), error.lines());
        assertNull(error.interrogationId());
    }

    @Test
    void validatePriorityRules_shouldReturnDuplicateIdError_whenSameIdAppearsMultipleTimes() {
        UUID duplicatedId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        questioningServiceStub.setQuestionings(List.of(
                questioning(duplicatedId),
                questioning(otherId)
        ));

        List<ValidationQuestioningPriorityError> errors =
                questioningPriorityService.validatePriorityRules(List.of(
                        priority(duplicatedId),
                        priority(duplicatedId),
                        priority(otherId)
                ));

        assertEquals(1, errors.size());

        ValidationQuestioningPriorityError error = errors.getFirst();
        assertEquals(ValidationQuestioningPriorityErrorType.DUPLICATE_INTERROGATION_ID, error.type());
        assertEquals(List.of(1, 2), error.lines());
        assertEquals(duplicatedId, error.interrogationId());
    }

    @Test
    void validatePriorityRules_shouldReturnUnknownIdErrors_whenIdsAreNotInQuestioningService() {
        UUID missingId = UUID.randomUUID();
        UUID existingId = UUID.randomUUID();

        questioningServiceStub.setQuestionings(List.of(
                questioning(existingId)
        ));

        List<ValidationQuestioningPriorityError> errors =
                questioningPriorityService.validatePriorityRules(List.of(
                        priority(missingId),
                        priority(existingId)
                ));

        assertEquals(1, errors.size());

        ValidationQuestioningPriorityError e1 = errors.getFirst();
        assertEquals(ValidationQuestioningPriorityErrorType.UNKNOWN_INTERROGATION_ID, e1.type());
        assertEquals(List.of(1), e1.lines());
        assertEquals(missingId, e1.interrogationId());
    }

    @Test
    void validatePriorityRules_shouldCombineNullDuplicateAndUnknownErrors() {
        UUID duplicatedAndMissingId = UUID.randomUUID();
        UUID existingId = UUID.randomUUID();

        questioningServiceStub.setQuestionings(List.of(
                questioning(existingId)
        ));

        List<ValidationQuestioningPriorityError> errors =
                questioningPriorityService.validatePriorityRules(List.of(
                        priority(null),
                        priority(duplicatedAndMissingId),
                        priority(existingId),
                        priority(duplicatedAndMissingId)
                ));

        assertEquals(4, errors.size());

        ValidationQuestioningPriorityError nullIdError = errors.getFirst();
        assertEquals(ValidationQuestioningPriorityErrorType.NULL_INTERROGATION_ID, nullIdError.type());
        assertEquals(List.of(1), nullIdError.lines());
        assertNull(nullIdError.interrogationId());

        ValidationQuestioningPriorityError duplicateError = errors.get(1);
        assertEquals(ValidationQuestioningPriorityErrorType.DUPLICATE_INTERROGATION_ID, duplicateError.type());
        assertEquals(List.of(2, 4), duplicateError.lines());
        assertEquals(duplicatedAndMissingId, duplicateError.interrogationId());

        ValidationQuestioningPriorityError unknownError1 = errors.get(2);
        assertEquals(ValidationQuestioningPriorityErrorType.UNKNOWN_INTERROGATION_ID, unknownError1.type());
        assertEquals(List.of(2), unknownError1.lines());
        assertEquals(duplicatedAndMissingId, unknownError1.interrogationId());

        ValidationQuestioningPriorityError unknownError2 = errors.get(3);
        assertEquals(ValidationQuestioningPriorityErrorType.UNKNOWN_INTERROGATION_ID, unknownError2.type());
        assertEquals(List.of(4), unknownError2.lines());
        assertEquals(duplicatedAndMissingId, unknownError2.interrogationId());
    }

    @Test
    void validatePriorityRules_shouldReturnEmpty_whenAllIdsAreValidAndUnique() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        questioningServiceStub.setQuestionings(List.of(
                questioning(id1),
                questioning(id2),
                questioning(id3)
        ));

        List<InterrogationPriorityInputDto> priorities = List.of(
                priority(id1),
                priority(id2),
                priority(id3)
        );

        List<ValidationQuestioningPriorityError> errors =
                questioningPriorityService.validatePriorityRules(priorities);

        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }
}