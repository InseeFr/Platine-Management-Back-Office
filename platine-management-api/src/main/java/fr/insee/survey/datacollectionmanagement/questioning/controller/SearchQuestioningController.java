package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchQuestioningParams;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.validation.SortValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || hasPermission(null, 'READ_SUPPORT')")
@Slf4j
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@RequiredArgsConstructor
@Validated
public class SearchQuestioningController {

    private final QuestioningService questioningService;

    private final SortValidator sortValidator;

    @Operation(summary = "Multi-criteria search questionings")
    @PostMapping(value = UrlConstants.API_QUESTIONINGS_SEARCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public Slice<SearchQuestioningDto> searchQuestionings(
            @RequestBody(required = false)
            SearchQuestioningParams searchParams,
            @ParameterObject
            @PageableDefault(page = 0, size = 50)
            Pageable pageable,
            @Parameter(hidden = true)
            @CurrentSecurityContext(expression = "authentication")
            Authentication authentication) {

        log.info("Search questionings with param {} pageable = {}",
                searchParams, pageable);

        String userId = authentication.getName().toUpperCase();

        Pageable sanitizedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortValidator.sanitizeSort(pageable.getSort())
        );

        return questioningService.searchQuestionings(searchParams, sanitizedPageable, userId);
    }

    @Operation(summary = "Get questioning details")
    @GetMapping(value = UrlConstants.API_QUESTIONINGS_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public QuestioningDetailsDto getQuestioning (@PathVariable("id") UUID id) {
        return questioningService.getQuestioningDetails(id);
    }
}
