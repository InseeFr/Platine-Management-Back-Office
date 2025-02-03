package fr.insee.survey.datacollectionmanagement.metadata.util;

import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SensitivityEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.UrlTypeEnum;
import fr.insee.survey.datacollectionmanagement.util.EmailValidatorRegex;
import fr.insee.survey.datacollectionmanagement.util.UrlValidator;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class ParamValidator {

    private ParamValidator() {
        throw new IllegalStateException("Validation class");
    }


    public static void validateParams(ParamsDto paramsDto) {
        if (paramsDto.getParamId().equalsIgnoreCase(ParameterEnum.URL_TYPE.name())
                && Arrays.stream(UrlTypeEnum.values()).noneMatch(p -> p.name().equals(paramsDto.getParamValue()))) {

            throw new NotMatchException(String.format("Only %s are valid values for URL_TYPE", Arrays.stream(UrlTypeEnum.values()).map(Enum::name)
                    .collect(joining(" "))));
        }
        if (paramsDto.getParamId().equalsIgnoreCase(ParameterEnum.URL_REDIRECTION.name())
                && !UrlValidator.isValidUrl(paramsDto.getParamValue())) {

            throw new NotMatchException(String.format("Url %s is not valid", paramsDto.getParamValue()));
        }
        if (paramsDto.getParamId().equalsIgnoreCase(ParameterEnum.MAIL_ASSISTANCE.name())
                && !EmailValidatorRegex.isValidEmail(paramsDto.getParamValue())) {

            throw new NotMatchException(String.format("Email %s is not valid", paramsDto.getParamValue()));
        }

        if (paramsDto.getParamId().equalsIgnoreCase(ParameterEnum.SENSITIVITY.name())
                && Arrays.stream(SensitivityEnum.values()).noneMatch(p -> p.name().equals(paramsDto.getParamValue()))) {

            throw new NotMatchException(String.format("Only %s are valid values for SENSITIVITY", Arrays.stream(SensitivityEnum.values()).map(Enum::name)
                    .collect(joining(" "))));
        }
    }


}