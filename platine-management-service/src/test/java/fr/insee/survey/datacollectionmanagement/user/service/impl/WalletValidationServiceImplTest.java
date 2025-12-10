package fr.insee.survey.datacollectionmanagement.user.service.impl;

import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.UserServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;
import fr.insee.survey.datacollectionmanagement.user.service.stub.SurveyUnitServiceStub;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WalletValidationServiceImplTest {


    private final UserServiceStub userService = new UserServiceStub();
    private final SurveyUnitServiceStub surveyUnitService = new SurveyUnitServiceStub();
    private final WalletValidationService walletValidationService = new WalletValidationServiceImpl(userService, surveyUnitService);

    @BeforeEach
    void setup() {
        User user = new User();
        user.setIdentifier("AAAAAA");
        User user2 = new User();
        user2.setIdentifier("BBBBBB");
        userService.setUsers(List.of(user, user2));
        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setIdSu("SU-001");
        SurveyUnit surveyUnit2 = new SurveyUnit();
        surveyUnit2.setIdSu("SU-002");
        surveyUnitService.setSurveyUnits(List.of(surveyUnit, surveyUnit2));

    }

    @Test
    void returnsEmpty_whenWalletsNullOrEmpty() {
        assertTrue(walletValidationService.validateDatabaseRules(null).isEmpty());
        assertTrue(walletValidationService.validateDatabaseRules(Collections.emptyList()).isEmpty());
    }

    @Test
    void returnsError_forMissingInternalUsers() {
        WalletDto w = new WalletDto("SU-001", "ZZZZZZ", "G1");
        List<ValidationWalletError> errors = walletValidationService.validateDatabaseRules(List.of(w));
        assertEquals(1, errors.size());
        ValidationWalletError e = errors.getFirst();
        assertEquals("idep", e.field());
        assertEquals("Unknown Internal Users: ZZZZZZ.", e.message());
    }

    @Test
    void returnsError_forMissingSurveyUnits() {
        WalletDto w = new WalletDto("SU-ZZZ", "AAAAAA", "G1");
        List<ValidationWalletError> errors = walletValidationService.validateDatabaseRules(List.of(w));
        assertEquals(1, errors.size());
        ValidationWalletError e = errors.getFirst();
        assertEquals("id_su", e.field());
        assertEquals("Unknown Survey Units: SU-ZZZ.", e.message());
    }

    @Test
    void returnsBothErrors_whenBothMissing() {
        WalletDto w = new WalletDto("SU-ZZZ", "ZZZZZZ", "G1");
        List<ValidationWalletError> errors = walletValidationService.validateDatabaseRules(List.of(w));
        assertEquals(2, errors.size());
        List<String> fields = errors.stream().map(ValidationWalletError::field).toList();
        assertTrue(fields.contains("idep"));
        assertTrue(fields.contains("id_su"));
        assertTrue(errors.stream().anyMatch(e -> e.message().contains("ZZZZZZ")));
        assertTrue(errors.stream().anyMatch(e -> e.message().contains("SU-ZZZ")));
    }

    @Test
    void returnsEmpty_whenNothingMissing() {
        WalletDto w1 = new WalletDto("SU-001", "AAAAAA", "G1");
        WalletDto w2 = new WalletDto("SU-002", "BBBBBB", "G1");
        List<ValidationWalletError> errors = walletValidationService.validateDatabaseRules(List.of(w1, w2));
        assertTrue(errors.isEmpty());
    }


}