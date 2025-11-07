package fr.insee.survey.datacollectionmanagement.user.service;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;

import java.util.List;
import java.util.Map;

public interface WalletValidationService {

    List<ValidationWalletError> validateDatabaseRules(List<WalletDto> wallets);
}