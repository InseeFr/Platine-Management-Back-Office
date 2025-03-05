package fr.insee.survey.datacollectionmanagement.metadata.validation;

import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataCollectionTargetValidatorTest {

    @Test
    void isNotValid() {
        DataCollectionTargetValidator dataCollectionTargetValidator = new DataCollectionTargetValidator();
        Assertions.assertFalse(dataCollectionTargetValidator.isValid("",null));
    }

    @Test
    void dataCollectionTargetLunaticNormal_isValid() {
        DataCollectionTargetValidator dataCollectionTargetValidator = new DataCollectionTargetValidator();
        Assertions.assertTrue(dataCollectionTargetValidator.isValid(DataCollectionEnum.LUNATIC_NORMAL.name(),null));
    }

    @Test
    void dataCollectionTargetLunaticSensitive_isValid() {
        DataCollectionTargetValidator dataCollectionTargetValidator = new DataCollectionTargetValidator();
        Assertions.assertTrue(dataCollectionTargetValidator.isValid(DataCollectionEnum.LUNATIC_SENSITIVE.name(),null));
    }

    @Test
    void dataCollectionTargetNull_isValid() {
        DataCollectionTargetValidator dataCollectionTargetValidator = new DataCollectionTargetValidator();
        Assertions.assertTrue(dataCollectionTargetValidator.isValid(null,null));
    }
}