package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommunicationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
public class QuestioningCommunicationController {

    private final QuestioningService questioningService;

    private final QuestioningCommunicationService questioningCommunicationService;

    @Operation(summary = "Search for questioning communications by questioning id")
    @GetMapping(value = Constants.API_QUESTIONING_ID_QUESTIONING_COMMUNICATIONS, produces = "application/json")
    public ResponseEntity<?> findQuestioningCommunicationsByQuestioningId(@PathVariable("id") Long id) {
        Questioning questioning = questioningService.findbyId(id);
        Set<QuestioningCommunication> setQe = questioning.getQuestioningCommunications();
        return ResponseEntity.status(HttpStatus.OK)
                .body(setQe.stream()
                        .map(questioningCommunicationService::convertToDto).toList());

    }





}
