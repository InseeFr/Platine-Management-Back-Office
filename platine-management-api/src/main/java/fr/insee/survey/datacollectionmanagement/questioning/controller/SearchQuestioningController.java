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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Slf4j
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@RequiredArgsConstructor
public class SearchQuestioningController {

    private final QuestioningService questioningService;

    @Operation(summary = "Multi-criteria search questionings")
    @PostMapping(value = UrlConstants.API_QUESTIONINGS_SEARCH, produces = "application/json")
    public Slice<SearchQuestioningDto> searchQuestionings(
            @RequestBody(required = false) SearchQuestioningParams searchParams,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info(
                "Search questionings with param {} page = {} pageSize = {}", searchParams, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        return questioningService.searchQuestionings(searchParams, pageable);

    }

    @Operation(summary = "Get questioning details")
    @GetMapping(value = UrlConstants.API_QUESTIONINGS_ID, produces = "application/json")
    public QuestioningDetailsDto getQuestioning (@PathVariable("id") Long id) {

        return questioningService.getQuestioningDetails(id);

    }


}
