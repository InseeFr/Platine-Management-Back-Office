package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommunicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
public class QuestioningCommunicationController {

    private final QuestioningCommunicationService questioningCommunicationService;

    @Operation(summary = "Search for questioning communications by questioning id")
    @GetMapping(value = UrlConstants.API_QUESTIONING_ID_QUESTIONING_COMMUNICATIONS, produces = "application/json")
    public List<QuestioningCommunicationDto> findQuestioningCommunicationsByQuestioningId(@PathVariable("id") Long id) {
        return questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(id);
    }


}
