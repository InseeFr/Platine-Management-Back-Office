package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchQuestioningParams;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@RequiredArgsConstructor
public class SearchQuestioningController {

    private final QuestioningService questioningService;

    @Operation(summary = "Multi-criteria search questionings")
    @PostMapping(value = UrlConstants.API_QUESTIONINGS_SEARCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || hasPermission(null, 'READ_SUPPORT')")
    public Slice<SearchQuestioningDto> searchQuestionings(
            @RequestBody(required = false) SearchQuestioningParams searchParams,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {

        log.info("Search questionings with param {} page = {} pageSize = {} sortBy = {} direction = {}",
                searchParams, page, pageSize, sortBy, sortDirection);

        Sort sort = Sort.unsorted();
        if (sortBy != null && sortDirection != null) {
            sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        }
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        return questioningService.searchQuestionings(searchParams, pageable);
    }

    @Operation(summary = "Get questioning details")
    @GetMapping(value = UrlConstants.API_QUESTIONINGS_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || hasPermission(null, 'READ_SUPPORT')")

    public QuestioningDetailsDto getQuestioning (@PathVariable("id") UUID id) {
        return questioningService.getQuestioningDetails(id);
    }
}
