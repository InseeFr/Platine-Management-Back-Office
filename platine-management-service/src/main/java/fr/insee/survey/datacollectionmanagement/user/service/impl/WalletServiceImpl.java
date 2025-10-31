package fr.insee.survey.datacollectionmanagement.user.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.user.service.WalletParserStrategy;
import fr.insee.survey.datacollectionmanagement.user.service.WalletService;
import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

  private final List<WalletParserStrategy> parserStrategies;
  private final WalletValidationService validationService;
  private final UserService userService;
  private final SurveyUnitService surveyUnitService;

  public WalletServiceImpl(
      List<WalletParserStrategy> parserStrategies,
      WalletValidationService validationService,
      UserService userService,
      SurveyUnitService surveyUnitService
  ) {
    this.parserStrategies = parserStrategies;
    this.validationService = validationService;
    this.userService = userService;
    this.surveyUnitService = surveyUnitService;
  }

  /**
   * STAGE 1: PARSING & SYNTAX VALIDATION
   * This is the renamed "importWallets" method.
   */
  @Override
  public List<WalletDto> parseAndValidateFile(MultipartFile file) {
    String filename = file.getOriginalFilename();
    if (filename == null || filename.isBlank()) {
      throw new IllegalArgumentException("Filename is missing");
    }

    log.info("Parsing and validating syntax for file: {}", filename);

    // 1. Find parsing strategy
    WalletParserStrategy strategy = parserStrategies.stream()
        .filter(p -> p.supports(filename))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "Unsupported file type. Allowed types are .csv, .json"
        ));

    // 2. Parse file to DTOs
    List<WalletDto> dtos = strategy.parse(file);

    // 3. Perform initial syntax validation
    log.info("Validating syntax for {} records...", dtos.size());
    int position = 0;
    for (WalletDto dto : dtos) {
      position++;
      validationService.validateFields(dto, position);
    }

    log.info("File parsed and syntax validated successfully.");
    return dtos;
  }

  @Transactional
  public void integrateWallets(String sourceId, List<WalletDto> dtos) {
    log.info("Starting integration of {} wallets for sourceId={}", dtos.size(), sourceId);
    //TODO integrate wallet
  }
}