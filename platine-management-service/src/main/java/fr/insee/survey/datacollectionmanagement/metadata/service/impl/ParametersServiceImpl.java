package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.ParametersService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParametersServiceImpl implements ParametersService {


    private final ModelMapper modelMapper;

    @Override
    public Parameters convertToEntity(ParamsDto paramsDto) {

        Parameters params = modelMapper.map(paramsDto, Parameters.class);
        params.setParamId(ParameterEnum.valueOf(paramsDto.getParamId()));
        return params;
    }

    @Override
    public ParamsDto convertToDto(Parameters params) {
        return modelMapper.map(params, ParamsDto.class);
    }

    @Override
    public Set<Parameters> updateCampaignParams(Campaign campaign, Parameters newParam) {
        Set<Parameters> updatedParams = campaign.getParams().stream()
                .filter(param -> !param.getParamId().equals(newParam.getParamId()))
                .collect(Collectors.toSet());

        updatedParams.add(newParam);
        return updatedParams;
    }

    @Override
    public Set<Parameters> updateSourceParams(Source source, Parameters newParam) {
        Set<Parameters> updatedParams = source.getParams().stream()
                .filter(param -> !param.getParamId().equals(newParam.getParamId()))
                .collect(Collectors.toSet());

        updatedParams.add(newParam);
        return updatedParams;
    }

    @Override
    public String findSuitableParameterValue(Partitioning part, ParameterEnum paramValue) {
        return findParameterValueInSet(part.getParams(), paramValue)
                .orElse(findParameterValueInSet(part.getCampaign().getParams(), paramValue)
                        .orElse(findParameterValueInSet(part.getCampaign().getSurvey().getParams(), paramValue)
                                .orElse(findParameterValueInSet(part.getCampaign().getSurvey().getSource().getParams(), paramValue)
                                        .orElse(""))));
    }



    private Optional<String> findParameterValueInSet(Set<Parameters> params, ParameterEnum paramValue) {
        return params.stream()
                .filter(param -> param.getParamId().equals(paramValue))
                .map(Parameters::getParamValue)
                .findFirst();
    }


}

