package fr.insee.survey.datacollectionmanagement.metadata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a single wallet assignment row.
 * This record handles JSON mapping and field trimming/cleaning.
 */
public record WalletDto(
    @NotBlank
    @JsonProperty("surveyUnit")
    String surveyUnit,
    @JsonProperty("internal_user")
    String internalUser,
    @JsonProperty("group")
    String group
) {

  /**
   * Canonical constructor that cleans incoming data.
   * Replaces nulls with empty strings and trims whitespace.
   */
  public WalletDto(String surveyUnit, String internalUser, String group) {
    this.surveyUnit = (surveyUnit == null) ? "" : surveyUnit.trim();
    this.internalUser = (internalUser == null) ? "" : internalUser.trim();
    this.group = (group == null) ? "" : group.trim();
  }
}