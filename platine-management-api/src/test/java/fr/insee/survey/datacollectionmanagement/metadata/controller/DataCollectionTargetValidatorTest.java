package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.validation.DataCollectionTargetValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DataCollectionTargetValidatorTest {

    @Test
    void isNotValid() {
        DataCollectionTargetValidator dataCollectionTargetValidator = new DataCollectionTargetValidator();
        assertFalse(dataCollectionTargetValidator.isValid("",null));
    }

    @Test
    void dataCollectionTargetLunaticNormal_isValid() {
        DataCollectionTargetValidator dataCollectionTargetValidator = new DataCollectionTargetValidator();
        assertTrue(dataCollectionTargetValidator.isValid(DataCollectionEnum.LUNATIC_NORMAL.name(),null));
    }

    @Test
    void dataCollectionTargetLunaticSensitive_isValid() {
        DataCollectionTargetValidator dataCollectionTargetValidator = new DataCollectionTargetValidator();
        assertTrue(dataCollectionTargetValidator.isValid(DataCollectionEnum.LUNATIC_SENSITIVE.name(),null));
    }

    @Test
    void dataCollectionTargetNull_isValid() {
        DataCollectionTargetValidator dataCollectionTargetValidator = new DataCollectionTargetValidator();
        assertTrue(dataCollectionTargetValidator.isValid(null,null));
    }
}