package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;

public record CampaignStatusDto(String id, CollectionStatus status) {
}

