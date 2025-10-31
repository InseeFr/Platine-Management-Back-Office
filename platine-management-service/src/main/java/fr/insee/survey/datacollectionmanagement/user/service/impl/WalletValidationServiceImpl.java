package fr.insee.survey.datacollectionmanagement.user.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.WalletBusinessRuleException;
import fr.insee.survey.datacollectionmanagement.metadata.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Component responsible for encapsulating all wallet import validation logic.
 * It checks field syntax, in-file business rules, and database consistency.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WalletValidationServiceImpl implements WalletValidationService {

  // Dependencies required *only* for database validation
  private final UserService userService;
  private final SurveyUnitService surveyUnitService;

  // Allowed: only letters and numbers
  private static final Pattern VALID_TEXT = Pattern.compile("^[A-Za-z0-9]+$");

  /**
   * Specification 1: Validates field syntax.
   * Checks that fields are non-blank and contain only allowed characters.
   */
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
   * Specification 2: Validates in-file business consistency rules (stateful).
   * - Rule 3: A SurveyUnit must be assigned to only one internalUser.
   * - Rule 4: An internalUser must be bound to only one group.
   */
  public void validateInFileRules(
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
   * Specification 3: Validates database consistency.
   * - Rule 1: All internalUsers must exist.
   * - Rule 2: All surveyUnits must exist.
   */
  public void validateDatabaseConsistency(
      Map<String, String> surveyToUser,
      Map<String, String> userToGroup) {

    log.info("Validating database existence for {} unique survey units and {} unique users...",
        surveyToUser.size(), userToGroup.size());

    Set<String> uniqueUsers = userToGroup.keySet();
    Set<String> uniqueSurveyUnits = surveyToUser.keySet();

    // Rule 1) CHECK UNIQUE MANAGERS (User)
    Set<String> missingUsers = userService.findMissingIdentifiers(uniqueUsers);

    // Rule 2) CHECK UNIQUE SURVEY UNITS (SurveyUnit)
    Set<String> missingSurveyUnits = surveyUnitService.findMissingIds(uniqueSurveyUnits);

    // AGGREGATE AND THROW ERROR
    if (!missingUsers.isEmpty() || !missingSurveyUnits.isEmpty()) {
      // Utilisez un point-virgule ou un point comme séparateur
      StringBuilder message = new StringBuilder("Database consistency validation failed. ");

      if (!missingUsers.isEmpty()) {
        message.append("Missing Internal Users: ").append(String.join(", ", missingUsers)).append(". ");
      }
      if (!missingSurveyUnits.isEmpty()) {
        message.append("Missing Survey Units: ").append(String.join(", ", missingSurveyUnits)).append(".");
      }

      // Enlever le dernier espace s'il y en a un
      throw new WalletBusinessRuleException(message.toString().trim());
    }

    log.info("Database consistency validated.");
  }
}