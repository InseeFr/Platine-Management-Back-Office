package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Upload;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @GetMapping(value = UrlConstants.API_QUESTIONING_ID_QUESTIONING_EVENTS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestioningEventDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public List<QuestioningEventDto> findQuestioningEventsByQuestioning(@PathVariable("id") Long id) {
        Questioning questioning = questioningService.findbyId(id);
        Set<QuestioningEvent> setQe = questioning.getQuestioningEvents();
        return setQe.stream().map(questioningEventService::convertToDto).toList();

    }

    @Operation(summary = "Create a questioning event")
    @PostMapping(value = UrlConstants.API_QUESTIONING_QUESTIONING_EVENTS, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = QuestioningEventDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public QuestioningEventDto postQuestioningEvent(@Parameter(description = "questioning id") Long id,
                                                    @RequestBody QuestioningEventDto questioningEventDto) {
        questioningService.findbyId(id);
        QuestioningEvent questioningEvent = questioningEventService.convertToEntity(questioningEventDto);
        QuestioningEvent newQuestioningEvent = questioningEventService.saveQuestioningEvent(questioningEvent);
        return questioningEventService.convertToDto(newQuestioningEvent);


    }

    @Operation(summary = "Delete a questioning event")
    @DeleteMapping(value = {UrlConstants.API_QUESTIONING_QUESTIONING_EVENTS_ID, UrlConstants.API_MOOG_DELETE_QUESTIONING_EVENT}, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<?> deleteQuestioningEvent(@PathVariable("id") Long id) {
        QuestioningEvent questioningEvent = questioningEventService.findbyId(id);

        Upload upload = (questioningEvent.getUpload() != null ? questioningEvent.getUpload() : null);
        Questioning quesitoning = questioningEvent.getQuestioning();
        quesitoning.setQuestioningEvents(quesitoning.getQuestioningEvents().stream()
                .filter(qe -> !qe.equals(questioningEvent)).collect(Collectors.toSet()));
        questioningService.saveQuestioning(quesitoning);
        questioningEventService.deleteQuestioningEvent(id);
        if (upload != null && questioningEventService.countIdUploadInEvents(upload.getId()) == 0) {
            uploadService.delete(upload);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Questioning event deleted");


    }

}
