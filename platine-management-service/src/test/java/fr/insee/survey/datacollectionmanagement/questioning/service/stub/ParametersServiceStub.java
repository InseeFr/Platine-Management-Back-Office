package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.ParametersService;

import java.util.Set;

public class ParametersServiceStub implements ParametersService {

    @Override
    public Parameters convertToEntity(ParamsDto paramsDto) {
        return null;
    }

    @Override
    public ParamsDto convertToDto(Parameters params) {
        return null;
    }

    @Override
    public Set<Parameters> updateCampaignParams(Campaign campaign, Parameters newParam) {
        return Set.of();
    }

    @Override
    public Set<Parameters> updateSourceParams(Source source, Parameters newParam) {
        return Set.of();
    }

    @Override
    public String findSuitableParameterValue(Partitioning part, ParameterEnum paramValue) {
        return "";
    }


}
