package fr.insee.survey.datacollectionmanagement.questioning.validation;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExpertEventValidatorTest {
    @Test
    void other_isNotValid() {
        IsExpertEventValidator validator = new IsExpertEventValidator();
        Assertions.assertFalse(validator.isValid(TypeQuestioningEvent.HC,null));
    }

    @Test
    void null_isNotValid() {
        IsExpertEventValidator validator = new IsExpertEventValidator();
        Assertions.assertFalse(validator.isValid(null,null));
    }

    @Test
    void expert_isValid() {
        IsExpertEventValidator validator = new IsExpertEventValidator();
        Assertions.assertTrue(validator.isValid(TypeQuestioningEvent.EXPERT,null));
    }

    @Test
    void ongexpert_isValid() {
        IsExpertEventValidator validator = new IsExpertEventValidator();
        Assertions.assertTrue(validator.isValid(TypeQuestioningEvent.ONGEXPERT,null));
    }

    @Test
    void valid_isValid() {
        IsExpertEventValidator validator = new IsExpertEventValidator();
        Assertions.assertTrue(validator.isValid(TypeQuestioningEvent.VALID,null));
    }

    @Test
    void endexpert_isValid() {
        IsExpertEventValidator validator = new IsExpertEventValidator();
        Assertions.assertTrue(validator.isValid(TypeQuestioningEvent.ENDEXPERT,null));
    }
}
