package fr.insee.survey.datacollectionmanagement.user.validator.stub;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class WalletValidationServiceStub implements WalletValidationService {

    @Setter
    private List<ValidationWalletError> toReturn = new ArrayList<>();

    @Override
    public List<ValidationWalletError> validateDatabaseRules(List<WalletDto> wallets) {
        return toReturn;
    }
}
