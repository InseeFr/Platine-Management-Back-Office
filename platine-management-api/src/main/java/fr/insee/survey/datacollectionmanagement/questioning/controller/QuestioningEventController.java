package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Upload;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ExpertEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadService;
import fr.insee.survey.datacollectionmanagement.questioning.validation.QuestioningEventTypeValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
public class QuestioningEventController {

    private final QuestioningEventService questioningEventService;

    private final QuestioningService questioningService;

    private final UploadService uploadService;

    @Operation(summary = "Search for a questioning event by questioning id")
    @GetMapping(value = UrlConstants.API_QUESTIONING_ID_QUESTIONING_EVENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestioningEventDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public List<QuestioningEventDto> findQuestioningEventsByQuestioning(@PathVariable("id") UUID id) {
        Questioning questioning = questioningService.findById(id);
        Set<QuestioningEvent> setQe = questioning.getQuestioningEvents();
        return setQe.stream().map(questioningEventService::convertToDto).toList();

    }

    @Operation(summary = "Create a questioning event")
    @PostMapping(value = UrlConstants.API_QUESTIONING_QUESTIONING_EVENTS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = QuestioningEventDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public QuestioningEventDto postQuestioningEvent(@Parameter(description = "questioning id") UUID id,
                                                    @RequestBody QuestioningEventDto questioningEventDto) {
        QuestioningEvent questioningEvent = questioningEventService.convertToEntity(questioningEventDto);
        QuestioningEvent newQuestioningEvent = questioningEventService.saveQuestioningEvent(questioningEvent);
        return questioningEventService.convertToDto(newQuestioningEvent);
    }

    @Operation(summary = "Create a questioning event")
    @PostMapping(value = UrlConstants.API_QUESTIONING_QUESTIONING_EVENTS_TYPE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = QuestioningEventDto.class))),
            @ApiResponse(responseCode = "200", description = "Updated", content = @Content(schema = @Schema(implementation = QuestioningEventDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Questioning not found")
    })
    public ResponseEntity<Void> createQuestioningEvent( @QuestioningEventTypeValid @PathVariable("eventType") String eventType,@RequestBody QuestioningEventInputDto questioningEventInputDto) {
        if (questioningEventService.postQuestioningEvent(eventType, questioningEventInputDto)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a questioning event")
    @DeleteMapping(value = {UrlConstants.API_QUESTIONING_QUESTIONING_EVENTS_ID, UrlConstants.API_MOOG_DELETE_QUESTIONING_EVENT}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<String> deleteQuestioningEvent(@PathVariable("id") Long id,
                                                         @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        QuestioningEvent questioningEvent = questioningEventService.findbyId(id);

        List<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Upload upload = questioningEvent.getUpload();

        questioningEventService.deleteQuestioningEventIfSpecificRole(userRoles, questioningEvent.getId(), questioningEvent.getType());
        if (upload != null && questioningEventService.countIdUploadInEvents(upload.getId()) == 0) {
            uploadService.delete(upload);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Questioning event deleted");
    }

    @Operation(summary = "Create a expert event [EXPERT, ONGEXPERT, VALID, ENDEXPERT, NOQUAL] for a questioning")
    @PostMapping(value = UrlConstants.API_QUESTIONING_ID_EXPERT_EVENTS,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expert event Created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Questioning not found"),
            @ApiResponse(responseCode = "500", description = "Internal Error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public void createExpertEvent(@PathVariable UUID id, @RequestBody @Valid ExpertEventDto expertEventDto) {
        log.info("Creation of a new expert event: type {}, score {}, score-init {}",
                expertEventDto.type(),
                expertEventDto.score(),
                expertEventDto.scoreInit());
        questioningEventService.postExpertEvent(id, expertEventDto);
    }

}
