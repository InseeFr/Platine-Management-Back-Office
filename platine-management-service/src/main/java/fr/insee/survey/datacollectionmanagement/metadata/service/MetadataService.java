package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.dto.BusinessMetadataDto;

public interface MetadataService {

    BusinessMetadataDto getBusinessMetadataDtoForCampaign(String campaignId);

}
