package fr.insee.survey.datacollectionmanagement.user.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.WalletBusinessRuleException;
import fr.insee.survey.datacollectionmanagement.metadata.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletValidationServiceImpl implements WalletValidationService {

  private final UserService userService;
  private final SurveyUnitService surveyUnitService;

  private static final Pattern VALID_TEXT = Pattern.compile("^[A-Za-z0-9]+$");

  /**
   * Specification 1: Validates field syntax.
   */
  @Override
  public void validateFields(WalletDto dto, int position) {
    if (dto.surveyUnit().isBlank() || dto.internalUser().isBlank() || dto.group().isBlank()) {
      throw new IllegalArgumentException(
          "Record " + position + ": all columns must be non-null and non-empty");
    }
    if (!VALID_TEXT.matcher(dto.surveyUnit()).matches()
        || !VALID_TEXT.matcher(dto.internalUser()).matches()
        || !VALID_TEXT.matcher(dto.group()).matches()) {
      throw new IllegalArgumentException(
          "Record " + position + ": fields contain forbidden special characters");
    }
  }

  /**
   * Specifications 2 & 3: Validates business rules and database consistency.
   */
  @Override
  public Map<String, String> validateBusinessAndDatabaseRules(List<WalletDto> dtos) {

    // --- Stage 2: In-File Business Rules ---
    log.info("Validating in-file business rules for {} records...", dtos.size());
    Map<String, String> surveyToUser = new HashMap<>();
    Map<String, String> userToGroup = new HashMap<>();
    int position = 0;
    for (WalletDto dto : dtos) {
      position++;
      validateInFileRules(dto, "item " + position, surveyToUser, userToGroup);
    }

    // --- Stage 3: Database Consistency ---
    log.info("Validating database consistency...");
    validateDatabaseConsistency(surveyToUser, userToGroup);

    // Return the map needed for processing
    return surveyToUser;
  }

  /**
   * Private helper for in-file rule validation (Rule 3 & 4).
   */
  private void validateInFileRules(
      WalletDto dto,
      String where,
      Map<String, String> surveyToUser,
      Map<String, String> userToGroup
  ) {
    // Rule 3) SurveyUnit -> only one manager
    String alreadyUser = surveyToUser.putIfAbsent(dto.surveyUnit(), dto.internalUser());
    if (alreadyUser != null && !alreadyUser.equals(dto.internalUser())) {
      throw new WalletBusinessRuleException(
          "surveyUnit '" + dto.surveyUnit() + "' already assigned to '" + alreadyUser +
              "' (conflict at " + where + ")"
      );
    }

    // Rule 4) manager -> only one group
    String alreadyGroup = userToGroup.putIfAbsent(dto.internalUser(), dto.group());
    if (alreadyGroup != null && !alreadyGroup.equalsIgnoreCase(dto.group())) {
      throw new WalletBusinessRuleException(
          "internalUser '" + dto.internalUser() + "' already bound to group '" + alreadyGroup +
              "' (conflict with '" + dto.group() + "' at " + where + ")"
      );
    }
  }

  /**
   * Private helper for database consistency validation (Rule 1 & 2).
   */
  private void validateDatabaseConsistency(
      Map<String, String> surveyToUser,
      Map<String, String> userToGroup) {

    Set<String> uniqueUsers = userToGroup.keySet();
    Set<String> uniqueSurveyUnits = surveyToUser.keySet();

    if (uniqueUsers.isEmpty() || uniqueSurveyUnits.isEmpty()) {
      log.warn("Validation skipped: no unique users or survey units found.");
      return;
    }

    log.info("Validating database existence for {} unique survey units and {} unique users...",
        uniqueSurveyUnits.size(), uniqueUsers.size());

    Set<String> missingUsers = userService.findMissingIdentifiers(uniqueUsers);
    Set<String> missingSurveyUnits = surveyUnitService.findMissingIds(uniqueSurveyUnits);

    if (!missingUsers.isEmpty() || !missingSurveyUnits.isEmpty()) {
      StringBuilder message = new StringBuilder("Database consistency validation failed. ");
      if (!missingUsers.isEmpty()) {
        message.append("Missing Internal Users: ").append(String.join(", ", missingUsers)).append(". ");
      }
      if (!missingSurveyUnits.isEmpty()) {
        message.append("Missing Survey Units: ").append(String.join(", ", missingSurveyUnits)).append(".");
      }
      throw new WalletBusinessRuleException(message.toString().trim());
    }

    log.info("Database consistency validated.");
  }
}