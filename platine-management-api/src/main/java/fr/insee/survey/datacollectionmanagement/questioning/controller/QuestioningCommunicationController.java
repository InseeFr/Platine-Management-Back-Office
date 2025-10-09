package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommunicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
public class QuestioningCommunicationController {

    private final QuestioningCommunicationService questioningCommunicationService;

    @Operation(summary = "Search for questioning communications by questioning id")
    @GetMapping(value = UrlConstants.API_QUESTIONING_ID_QUESTIONING_COMMUNICATIONS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<QuestioningCommunicationDto> findQuestioningCommunicationsByQuestioningId(@PathVariable("id") UUID id) {
        return questioningCommunicationService.findQuestioningCommunicationsByQuestioningId(id);
    }

    @Operation(summary = "Create a questioning communication")
    @PostMapping(value = UrlConstants.API_QUESTIONING_QUESTIONING_COMMUNICATION_TYPE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = QuestioningCommunicationDto.class))),
        @ApiResponse(responseCode = "200", description = "Updated", content = @Content(schema = @Schema(implementation = QuestioningCommunicationDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Questioning not found")
    })
    public ResponseEntity<Void> createQuestioningCommunication(@PathVariable("communicationType") String communicationType, @RequestBody QuestioningCommunicationInputDto questioningCommunicationInputDto) {
      if (questioningCommunicationService.postQuestioningCommunication(communicationType, questioningCommunicationInputDto)) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
      }
      return ResponseEntity.ok().build();
    }


}
