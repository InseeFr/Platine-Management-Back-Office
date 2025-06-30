package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;

public record CampaignCommonsOngoingDto(
        @NotBlank String id,
        @NotBlank String dataCollectionTarget,
        @NotBlank boolean sensitivity,
        @NotBlank String collectMode
) {}