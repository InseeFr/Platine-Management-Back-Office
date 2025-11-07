package fr.insee.survey.datacollectionmanagement.user.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.user.dao.WalletDao;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.service.WalletParserStrategy;
import fr.insee.survey.datacollectionmanagement.user.service.WalletService;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final List<WalletParserStrategy> parserStrategies;
    private final WalletDao walletDao;
    private final SourceRepository sourceRepository;

    public List<WalletDto> parse(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename is missing");
        }

        log.info("Parsing for file: {}", filename);

        // 1. Find parsing strategy
        WalletParserStrategy strategy = parserStrategies.stream()
                .filter(p -> p.supports(filename))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported file type. Allowed types are .csv, .json"
                ));

        // 2. Parse file to DTOs
        return strategy.parse(file);
    }

    @Override
    public void integrateWallets(String sourceId, List<WalletDto> wallets) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new NotFoundException("Source not found: " + sourceId));
        if (wallets == null || wallets.isEmpty()) {
            log.info("No wallets provided for source {} — cleaning existing data", sourceId);
            walletDao.cleanData(source.getId());
            return;
        }
        walletDao.upsertWallets(source, wallets);
    }
}