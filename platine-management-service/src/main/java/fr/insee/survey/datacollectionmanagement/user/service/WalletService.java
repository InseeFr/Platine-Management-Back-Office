package fr.insee.survey.datacollectionmanagement.user.service;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import java.util.List;

public interface WalletService {

    void integrateWallets(String sourceId, List<WalletDto> wallets);
}