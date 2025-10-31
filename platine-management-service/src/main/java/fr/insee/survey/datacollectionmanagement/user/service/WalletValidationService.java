package fr.insee.survey.datacollectionmanagement.user.service;

import fr.insee.survey.datacollectionmanagement.metadata.dto.WalletDto;
import java.util.List;
import java.util.Map;

public interface WalletValidationService {

  /**
   * Specification 1: Validates field syntax (non-blank, allowed chars).
   * This is called during the parsing phase.
   */
  void validateFields(WalletDto dto, int position);

  /**
   * Specifications 2 & 3: Validates business rules and database consistency.
   * This is called during the integration phase.
   *
   * @param dtos The full list of DTOs to validate.
   * @return A map of [SurveyUnitID -> UserID] built during validation,
   * to be used by the processing step.
   */
  Map<String, String> validateBusinessAndDatabaseRules(List<WalletDto> dtos);
}