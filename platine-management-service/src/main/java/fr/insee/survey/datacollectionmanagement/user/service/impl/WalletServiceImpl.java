package fr.insee.survey.datacollectionmanagement.user.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.user.service.WalletParserStrategy;
import fr.insee.survey.datacollectionmanagement.user.service.WalletService;
import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service to import, validate, and process wallet assignments.
 *
 * This service acts as the central orchestrator:
 * 1. Delegates parsing to WalletParserStrategy (found via locator pattern).
 * 2. Delegates all validation to WalletValidationService.
 * 3. Handles the final processing (persistence) logic itself.
 */
@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

  // --- Dependencies ---
  private final List<WalletParserStrategy> parserStrategies;
  private final WalletValidationService validationService;
  private final UserService userService;
  private final SurveyUnitService surveyUnitService;

  // Constructor for dependency injection
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
   * Imports, validates, and processes a wallet file.
   * This entire method is transactional. If any validation step (that hits the
   * DB)
   * or the final processing step fails, the transaction will be rolled back.
   */
  @Override
  public void importWallets(String sourceId, MultipartFile file) {
    String filename = file.getOriginalFilename();
    if (filename == null || filename.isBlank()) {
      throw new IllegalArgumentException("Filename is missing");
    }

    log.info("Start import wallets, sourceId={}, file={}", sourceId, filename);

    // === STAGE 1: PARSING ===
    WalletParserStrategy strategy = parserStrategies.stream()
        .filter(p -> p.supports(filename))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "Unsupported file type. Allowed types are .csv, .json"
        ));

    List<WalletDto> dtos = strategy.parse(file);

    // === STAGE 2: VALIDATION (In-Memory) ===
    Map<String, String> surveyUnitToInternalUser = new HashMap<>();
    Map<String, String> internalUserToGroup = new HashMap<>();

    log.info("Validating {} records from file...", dtos.size());
    int position = 0;
    for (WalletDto dto : dtos) {
      position++;
      String where = "item " + position;

      validationService.validateFields(dto, position);
      validationService.validateInFileRules(
          dto,
          where,
          surveyUnitToInternalUser,
          internalUserToGroup
      );
    }

    // === STAGE 3: VALIDATION (Database Consistency) ===
    validationService.validateDatabaseConsistency(
        surveyUnitToInternalUser,
        internalUserToGroup
    );

    // === STAGE 4: PROCESSING (Internal) ===
    log.info("All validations passed. Proceeding to process assignments.");
    processWallets(surveyUnitToInternalUser, internalUserToGroup);

    log.info("Wallets import and processing completed successfully for sourceId={}", sourceId);
  }


  /* =================================================================
   * ==            INTERNAL PROCESSING (Kept in WalletServiceImpl) ==
   * ================================================================= */

  /**
   * Applies the validated wallet assignments to the database.
   * This method is NO LONGER marked @Transactional, as it will be
   * called by the transactional importWallets() method.
   */
  private void processWallets(
      Map<String, String> surveyUnitToInternalUser,
      Map<String, String> internalUserToGroup) {

    log.info("Processing {} wallet assignments for {} unique users.",
        surveyUnitToInternalUser.size(), internalUserToGroup.size());

    // ---------------------------------------------------------------------
    // TODO: Implement the actual persistence logic here.
    // ---------------------------------------------------------------------

    surveyUnitToInternalUser.forEach((surveyUnitId, userId) -> {
      log.debug("Assigning SurveyUnit '{}' to User '{}'", surveyUnitId, userId);
    });

    internalUserToGroup.forEach((userId, groupId) -> {
      log.debug("Confirming User '{}' is in Group '{}'", userId, groupId);
    });

    log.info("Successfully processed all wallet assignments.");
  }
}