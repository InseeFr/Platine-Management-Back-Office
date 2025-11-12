package fr.insee.survey.datacollectionmanagement.user.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.WalletBusinessRuleException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.user.dao.WalletDao;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.service.WalletService;

import java.util.List;

import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletDao walletDao;
    private final SourceRepository sourceRepository;
    private final WalletValidationService walletValidationService;

    @Override
    public void integrateWallets(String sourceId, List<WalletDto> wallets) {
        log.info("Validating objects existence");
        List<ValidationWalletError> notExistenceErrors = walletValidationService.validateDatabaseRules(wallets);
        if (!notExistenceErrors.isEmpty()) {
            List<String> errorMessages = notExistenceErrors.stream()
                    .map(ValidationWalletError::toString)
                    .toList();
            errorMessages.forEach(log::error);
            throw new WalletBusinessRuleException("Invalid Data", errorMessages);
        }

        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new NotFoundException("Source not found: " + sourceId));
        walletDao.cleanData(source.getId());
        if (wallets == null || wallets.isEmpty()) {
            log.info("No wallets provided for source {} — cleaning existing data", sourceId);
            return;
        }
        walletDao.insertWallets(source, wallets);
    }
}