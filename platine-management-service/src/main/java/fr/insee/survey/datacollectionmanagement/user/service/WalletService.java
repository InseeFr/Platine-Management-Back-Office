package fr.insee.survey.datacollectionmanagement.user.service;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface WalletService {

    List<WalletDto> parse(MultipartFile file);
    void integrateWallets(String sourceId, List<WalletDto> wallets);
}