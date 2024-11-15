package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningComment;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommentService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Set;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class QuestioningCommentController {

    private final QuestioningService questioningService;
    private final QuestioningCommentService questioningCommentService;
    private final ModelMapper modelMapper;


    @Operation(summary = "Create a questioning comment")
    @PostMapping(value = Constants.API_QUESTIONING_ID_COMMENT, produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = QuestioningCommentInputDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public void postQuestioningComment(@PathVariable Long id, @Valid @RequestBody QuestioningCommentInputDto questioningCommentDto) {

        Questioning questioning = questioningService.findbyId(id);
        QuestioningComment questioningComment = questioningCommentService.convertToEntity(questioningCommentDto);
        questioningComment.setDate(new Date());
        QuestioningComment newQuestioningComment = questioningCommentService.saveQuestioningComment(questioningComment);
        Set<QuestioningComment> setQuestioningComments = questioning.getQuestioningComments();
        setQuestioningComments.add(newQuestioningComment);
        questioning.setQuestioningComments(setQuestioningComments);
        questioningService.saveQuestioning(questioning);

    }




}
