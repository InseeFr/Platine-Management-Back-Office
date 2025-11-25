package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.InterrogationPriorityInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.ValidationQuestioningPriorityErrorType;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningPriorityService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.validation.ValidationQuestioningPriorityError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestioningPriorityServiceImpl  implements QuestioningPriorityService {

    private final QuestioningService questioningService;

    @Override
    public List<ValidationQuestioningPriorityError> validatePriorityRules(
            List<InterrogationPriorityInputDto> priorities) {

        if (priorities == null || priorities.isEmpty()) {
            log.info("Validating questioning priorities: no record");
            return List.of();
        }

        List<LinePriority> linePriorities = toLinePriorities(priorities);

        List<ValidationQuestioningPriorityError> nullIdErrors = validateNullIds(linePriorities);
        if (!nullIdErrors.isEmpty()) {
            log.info("Found {} record(s) with null interrogation id", nullIdErrors.size());
        }
        List<ValidationQuestioningPriorityError> errors = new ArrayList<>(nullIdErrors);

        List<ValidationQuestioningPriorityError> duplicateIdErrors = validateDuplicateIds(linePriorities);
        if (!duplicateIdErrors.isEmpty()) {
            log.info("Found {} duplicate interrogation id error(s)", duplicateIdErrors.size());
        }
        errors.addAll(duplicateIdErrors);

        List<ValidationQuestioningPriorityError> unknownIdErrors = validateUnknownIds(linePriorities);
        if (!unknownIdErrors.isEmpty()) {
            log.info("Found {} unknown interrogation id error(s)", unknownIdErrors.size());
        }
        errors.addAll(unknownIdErrors);

        if (errors.isEmpty()) {
            log.info("Priority validation completed successfully, no errors found");
            return List.of();
        }

        log.warn("Priority validation completed with {} error(s)", errors.size());
        return errors;
    }

    private List<LinePriority> toLinePriorities(List<InterrogationPriorityInputDto> priorities) {
        return IntStream.range(0, priorities.size())
                .mapToObj(i -> new LinePriority(i + 1, priorities.get(i)))
                .toList();
    }

    private List<ValidationQuestioningPriorityError> validateNullIds(List<LinePriority> linePriorities) {
        return linePriorities.stream()
                .filter(lp -> lp.input().interrogationId() == null)
                .map(lp -> new ValidationQuestioningPriorityError(
                        ValidationQuestioningPriorityErrorType.INTERROGATION_ID_NULL,
                        lp.lineNumber()
                ))
                .toList();
    }

    private List<ValidationQuestioningPriorityError> validateDuplicateIds(List<LinePriority> linePriorities) {
        Map<UUID, List<Integer>> linesById = linePriorities.stream()
                .filter(lp -> lp.input().interrogationId() != null)
                .collect(Collectors.groupingBy(
                        lp -> lp.input().interrogationId(),
                        Collectors.mapping(LinePriority::lineNumber, Collectors.toList())
                ));

        return linesById.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> new ValidationQuestioningPriorityError(
                        ValidationQuestioningPriorityErrorType.DUPLICATE_INTERROGATION_ID,
                        e.getValue(),
                        e.getKey()
                ))
                .toList();
    }

    private List<ValidationQuestioningPriorityError> validateUnknownIds(List<LinePriority> linePriorities) {
        Set<UUID> ids = linePriorities.stream()
                .map(lp -> lp.input().interrogationId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ids.isEmpty()) {
            return List.of();
        }

        Set<UUID> missingIds = questioningService.findMissingIds(ids);
        if (missingIds.isEmpty()) {
            return List.of();
        }

        return linePriorities.stream()
                .filter(lp -> {
                    UUID id = lp.input().interrogationId();
                    return id != null && missingIds.contains(id);
                })
                .map(lp -> new ValidationQuestioningPriorityError(
                        ValidationQuestioningPriorityErrorType.UNKNOWN_INTERROGATION_ID,
                        lp.lineNumber(),
                        lp.input().interrogationId()
                ))
                .toList();
    }

    private record LinePriority(int lineNumber, InterrogationPriorityInputDto input) {}
}
