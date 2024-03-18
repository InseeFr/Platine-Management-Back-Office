package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.AccreditationDetailDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchContactDto;
import fr.insee.survey.datacollectionmanagement.query.service.MoogService;
import fr.insee.survey.datacollectionmanagement.query.service.SearchContactService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.util.TypeQuestioningEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@Tag(name = "4 - Cross domain")
@Slf4j
@RequiredArgsConstructor
public class SearchContactController {

    private final SearchContactService searchContactService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final PartitioningService partitioningService;

    private final QuestioningEventService questioningEventService;

    private final MoogService moogService;

    @GetMapping(path = Constants.API_CONTACTS_SEARCH, produces = "application/json")
    @Operation(summary = "Multi-criteria search contacts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SearchContactDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Page<SearchContactDto>> searchContacts(
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "identifier")  String sort) {

        log.info(
                "Search contact: identifier = {}, name= {}, email= {}, page= {}, pageSize= {} ",
                identifier, name, email, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sort));

        Page<SearchContactDto> pageSearchContact = searchContactService.searchContactCrossDomain(identifier, name, email,
                pageable);
        return new ResponseEntity<>(pageSearchContact, HttpStatus.OK);


    }

    @GetMapping(path = Constants.API_CONTACTS_ACCREDITATIONS, produces = "application/json")
    @Operation(summary = "Get contact accreditations by the contact id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccreditationDetailDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<List<AccreditationDetailDto>> getContactAccreditations(
            @PathVariable("id") String id,
            @RequestParam(defaultValue = "false") boolean isFilterOpened) {

        List<AccreditationDetailDto> listAccreditations = new ArrayList<>();
        List<QuestioningAccreditation> accreditations = questioningAccreditationService.findByContactIdentifier(id);
        for (QuestioningAccreditation questioningAccreditation : accreditations) {
            Questioning questioning = questioningAccreditation.getQuestioning();
            Partitioning part = partitioningService.findById(questioning.getIdPartitioning());
            Optional<QuestioningEvent> questioningEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.STATE_EVENTS);

            if (!isFilterOpened || partitioningService.isOnGoing(part, new Date())) {
                SurveyUnit su = questioningAccreditation.getQuestioning().getSurveyUnit();
                Survey survey = part.getCampaign().getSurvey();
                listAccreditations.add(new AccreditationDetailDto(
                        survey.getSource().getId(),
                        survey.getId(),
                        survey.getSource().getShortWording(),
                        survey.getYear(),
                        part.getCampaign().getPeriod(),
                        part.getId(),
                        part.getClosingDate(),
                        su.getIdSu(),
                        su.getIdentificationName(),
                        questioningAccreditation.isMain(),
                        questioningEvent.map(QuestioningEvent::getType).orElse(null),
                        questioning.getId().toString(),
                        moogService.getReadOnlyUrl(part.getCampaign().getId(), su.getIdSu())
                ));
            }

        }

        return new ResponseEntity<>(listAccreditations, HttpStatus.OK);

    }

}
