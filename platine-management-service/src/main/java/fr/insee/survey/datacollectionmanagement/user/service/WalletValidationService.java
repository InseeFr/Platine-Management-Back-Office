package fr.insee.survey.datacollectionmanagement.user.service;

import fr.insee.survey.datacollectionmanagement.metadata.dto.WalletDto;
import java.util.Map;

/**
 * Interface for the component responsible for encapsulating all wallet import
 * validation logic.
 * It defines the contract for checking field syntax, in-file business rules,
 * and database consistency.
 */
public interface WalletValidationService {

  /**
   * Specification 1: Validates field syntax.
   * Checks that fields are non-blank and contain only allowed characters.
   */
  void validateFields(WalletDto dto, int position);

  /**
   * Specification 2: Validates in-file business consistency rules (stateful).
   * - Rule 3: A SurveyUnit must be assigned to only one internalUser.
   * - Rule 4: An internalUser must be bound to only one group.
   */
  void validateInFileRules(
      WalletDto dto,
      String where,
      Map<String, String> surveyToUser,
      Map<String, String> userToGroup
  );

  /**
   * Specification 3: Validates database consistency.
   * - Rule 1: All internalUsers must exist.
   * - Rule 2: All surveyUnits must exist.
   */
  void validateDatabaseConsistency(
      Map<String, String> surveyToUser,
      Map<String, String> userToGroup
  );
}