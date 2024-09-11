package fr.insee.survey.datacollectionmanagement.metadata.util;

import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.questioning.util.UrlRedirectionEnum;
import fr.insee.survey.datacollectionmanagement.questioning.util.UrlTypeEnum;
import fr.insee.survey.datacollectionmanagement.util.EmailValidatorRegex;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class ParamValidator {

    private ParamValidator() {
        throw new IllegalStateException("Validation class");
    }

    public static void validateParams(ParamsDto paramsDto) {
        if (paramsDto.getParamId().equalsIgnoreCase(Parameters.ParameterEnum.URL_TYPE.name())
                && Arrays.stream(UrlTypeEnum.values()).noneMatch(p -> p.name().equals(paramsDto.getParamValue()))) {

            throw new NotMatchException(String.format("Only %s are valid values for URL_TYPE", Arrays.stream(UrlTypeEnum.values()).map(Enum::name)
                    .collect(joining(" "))));
        }
        if (paramsDto.getParamId().equalsIgnoreCase(Parameters.ParameterEnum.URL_REDIRECTION.name())
                && Arrays.stream(UrlRedirectionEnum.values()).noneMatch(p -> p.name().equals(paramsDto.getParamValue()))) {

            throw new NotMatchException(String.format("Only %s are valid values for URL_REDIRECTION", Arrays.stream(UrlRedirectionEnum.values()).map(Enum::name)
                    .collect(joining(" "))));
        }
        if (paramsDto.getParamId().equalsIgnoreCase(Parameters.ParameterEnum.MAIL_ASSISTANCE.name())
                && !EmailValidatorRegex.isValidEmail(paramsDto.getParamValue())) {

            throw new NotMatchException(String.format("Email %s is not valid", paramsDto.getParamValue()));
        }
    }
}

