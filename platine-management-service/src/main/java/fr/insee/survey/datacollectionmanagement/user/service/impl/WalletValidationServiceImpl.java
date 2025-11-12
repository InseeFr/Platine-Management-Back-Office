package fr.insee.survey.datacollectionmanagement.user.service.impl;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;

import java.util.*;
import java.util.stream.Collectors;

import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletValidationServiceImpl implements WalletValidationService {

    private final UserService userService;
    private final SurveyUnitService surveyUnitService;

    @Override
    public List<ValidationWalletError> validateDatabaseRules(List<WalletDto> wallets) {
        if (wallets == null || wallets.isEmpty()) {
            return List.of();
        }

        List<ValidationWalletError> errors = new ArrayList<>();

        Set<String> userIds = wallets.stream()
                .filter(Objects::nonNull)
                .map(WalletDto::internalUser)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        Set<String> missingUsers = userService.findMissingIdentifiers(userIds);
        if (!missingUsers.isEmpty()) {
            String error = String.format("Unknown Internal Users: %s.", String.join(", ", missingUsers));
            errors.add(new ValidationWalletError("internal_user", error));
        }

        Set<String> suIds = wallets.stream()
                .map(WalletDto::surveyUnit)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toSet());
        Set<String> missingSurveyUnits = surveyUnitService.findMissingIds(suIds);
        if (!missingSurveyUnits.isEmpty()) {
            String error = String.format("Unknown Survey Units: %s.", String.join(", ", missingSurveyUnits));
            errors.add(new ValidationWalletError("survey_unit", error));
        }
        return errors;
    }
}