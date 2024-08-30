package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;

import java.util.Set;

public interface ParametersService {
    Parameters convertToEntity(ParamsDto paramsDto);

    ParamsDto convertToDto(Parameters params);

    Set<Parameters> updateCampaignParams(Campaign campaign, Parameters newParam);

    Set<Parameters> updateSourceParams(Source source, Parameters newParam);
}
