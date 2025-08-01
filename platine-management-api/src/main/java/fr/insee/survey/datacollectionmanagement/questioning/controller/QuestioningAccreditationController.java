package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningAccreditationDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
public class QuestioningAccreditationController {

    private final QuestioningAccreditationService questioningAccreditationService;

    private final QuestioningService questioningService;

    private final ContactService contactService;

    private final PartitioningService partitioningService;

    private final ViewService viewService;

    private final ModelMapper modelMapper;


    /**
     * @deprecated
     */
    @Operation(summary = "Search for questioning accreditations by questioning id")
    @GetMapping(value = UrlConstants.API_QUESTIONINGS_ID_QUESTIONING_ACCREDITATIONS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = QuestioningAccreditationDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @Deprecated(since = "2.6.0")
    public List<QuestioningAccreditationDto> getQuestioningAccreditation(@PathVariable("id") UUID id) {
        log.warn("DEPRECATED");

        Questioning optQuestioning = questioningService.findById(id);


        return
                optQuestioning.getQuestioningAccreditations().stream().map(this::convertToDto)
                        .toList();


    }


    /**
     * @deprecated
     */
    @Operation(summary = "Create or update a questioning accreditation for a questioning")
    @PostMapping(value = UrlConstants.API_QUESTIONINGS_ID_QUESTIONING_ACCREDITATIONS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = QuestioningAccreditationDto.class))),
            @ApiResponse(responseCode = "404", description = "NotFound")
    })
    @Transactional
    @Deprecated(since = "2.6.0")
    public ResponseEntity<QuestioningAccreditationDto> postQuestioningAccreditation(@PathVariable("id") UUID id,
                                                                                    @RequestBody QuestioningAccreditationDto questioningAccreditationDto) {

        log.warn("DEPRECATED");
        Questioning questioning = questioningService.findById(id);

        String idContact = questioningAccreditationDto.getIdContact();
        contactService.findByIdentifier(idContact);

        HttpHeaders responseHeaders = new HttpHeaders();

        // save new accreditation or update existing one
        Set<QuestioningAccreditation> setExistingAccreditations = questioning.getQuestioningAccreditations();
        Partitioning part = partitioningService.findById(questioning.getIdPartitioning());

        String idSu = questioning.getSurveyUnit().getIdSu();

        List<QuestioningAccreditation> listContactAccreditations = setExistingAccreditations.stream()
                .filter(acc -> acc.getIdContact().equals(idContact)
                               && acc.getQuestioning().getIdPartitioning().equals(part.getId())
                               && acc.getQuestioning().getSurveyUnit().getIdSu().equals(idSu))
                .toList();

        if (listContactAccreditations.isEmpty()) {
            // Create new accreditation
            QuestioningAccreditation questioningAccreditation = convertToEntity(questioningAccreditationDto);
            questioningAccreditation.setQuestioning(questioning);
            setExistingAccreditations.add(questioningAccreditation);
            questioningAccreditationService.saveQuestioningAccreditation(questioningAccreditation);
            questioningService.saveQuestioning(questioning);

            // create view
            viewService.createViewAndDeleteEmptyExistingOnesByIdentifier(idContact, questioning.getSurveyUnit().getIdSu(),
                    part.getCampaign().getId());

            // location header
            responseHeaders.set(HttpHeaders.LOCATION,
                    ServletUriComponentsBuilder.fromCurrentRequest().path(questioningAccreditation.getId().toString())
                            .toUriString());

            return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders)
                    .body(convertToDto(questioningAccreditation));


        } else {
            // update accreditation
            QuestioningAccreditation questioningAccreditation = listContactAccreditations.getFirst();
            questioningAccreditationDto.setId(questioningAccreditation.getId());
            questioningAccreditation = convertToEntity(questioningAccreditationDto);
            questioningAccreditation.setQuestioning(questioning);
            questioningAccreditationService.saveQuestioningAccreditation(questioningAccreditation);

            // view already exists

            // location header
            responseHeaders.set(HttpHeaders.LOCATION,
                    ServletUriComponentsBuilder.fromCurrentRequest().path(questioningAccreditation.getId().toString())
                            .toUriString());
            return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders)
                    .body(convertToDto(questioningAccreditation));
        }

    }

    private QuestioningAccreditation convertToEntity(QuestioningAccreditationDto questioningAccreditationDto) {
        QuestioningAccreditation questioningAccreditation = new QuestioningAccreditation();
        questioningAccreditation.setId(questioningAccreditationDto.getId());
        questioningAccreditation.setIdContact(questioningAccreditationDto.getIdContact());
        questioningAccreditation.setMain(questioningAccreditationDto.isMain());
        questioningAccreditation.setCreationAuthor(questioningAccreditationDto.getCreationAuthor());
        questioningAccreditation.setCreationDate(questioningAccreditationDto.getCreationDate());
        return questioningAccreditation;
    }

    private QuestioningAccreditationDto convertToDto(QuestioningAccreditation questioningAccreditation) {
        return modelMapper.map(questioningAccreditation, QuestioningAccreditationDto.class);
    }

}
