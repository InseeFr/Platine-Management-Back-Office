package fr.insee.survey.datacollectionmanagement.contact.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDetailsDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.SearchContactDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactParamEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.contact.util.PayloadUtil;
import fr.insee.survey.datacollectionmanagement.contact.validation.ValidContactParam;
import fr.insee.survey.datacollectionmanagement.exception.ImpossibleToDeleteException;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.Serial;
import java.util.Collections;
import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "1 - Contacts", description = "Endpoints to create, update, delete and find contacts")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ContactController {

    private final ContactService contactService;
    
    private final ViewService viewService;

    private final QuestioningAccreditationService questioningAccreditationService;


    /**
     * @deprecated
     */
    @Operation(summary = "Search for contacts, paginated")
    @GetMapping(value = Constants.API_CONTACTS_ALL, produces = "application/json")
    @Deprecated(since = "2.6.0", forRemoval = true)
    public ContactPage getContacts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "identifier") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Contact> pageC = contactService.findAll(pageable);
        List<ContactDto> listC = pageC.stream().map(contactService::convertToDto).toList();
        return new ContactPage(listC, pageable, pageC.getTotalElements());
    }

    @Operation(summary = "Search for a contact by its id")
    @GetMapping(value = Constants.API_CONTACTS_ID)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || " + AuthorityPrivileges.HAS_REPONDENT_LIMITATED_PRIVILEGES)
    public ContactDetailsDto getContact(@PathVariable("id") String id) {
        String idContact = StringUtils.upperCase(id);
        Contact contact = contactService.findByIdentifier(idContact);
        List<String> listCampaigns = viewService.findDistinctCampaignByIdentifier(idContact);
        return contactService.convertToContactDetailsDto(contact, listCampaigns);


    }


    @Operation(summary = "Update or create a contact")
    @PutMapping(value = Constants.API_CONTACTS_ID, produces = "application/json", consumes = "application/json")
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || " + AuthorityPrivileges.HAS_REPONDENT_LIMITATED_PRIVILEGES)
    public ResponseEntity<ContactDto> putContact(@PathVariable("id") String id,
                                                 @RequestBody @Valid ContactDto contactDto,
                                                 Authentication auth) throws JsonProcessingException {
        if (!contactDto.getIdentifier().equalsIgnoreCase(id)) {
            throw new NotMatchException("id and contact identifier don't match");
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION, ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(id).toUriString());

        JsonNode payload = PayloadUtil.getPayloadAuthor(auth.getName());
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            contactService.findByIdentifier(id);
        } catch (NotFoundException e) {
            log.info("Creating contact with the identifier {}", contactDto.getIdentifier());
            httpStatus = HttpStatus.CREATED;
        }
        Contact contact = contactService.updateOrCreateContact(id, contactDto, payload);


        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(contactService.convertToDto(contact));

    }


    /**
     * @deprecated
     */
    @Operation(summary = "Delete a contact, its address, its contactEvents")
    @DeleteMapping(value = Constants.API_CONTACTS_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated(since = "2.6.0", forRemoval = true)
    public void deleteContact(@PathVariable("id") String id) {

        if (!questioningAccreditationService.findByContactIdentifier(id).isEmpty()) {
            throw new ImpossibleToDeleteException(
                    String.format("Contact %s cannot be deleted as he/she is still entitled to answer one or more questionnaires", id));
        }

        log.info("Delete contact {}", id);
        Contact contact = contactService.findByIdentifier(id);
        contactService.deleteContactAddressEvent(contact);

    }

    @GetMapping(path = Constants.API_CONTACTS_SEARCH, produces = "application/json")
    @Operation(summary = "Multi-criteria search contacts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SearchContactDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public Page<SearchContactDto> searchContacts(
            @RequestParam(required = true) String searchParam,
            @RequestParam(required = false) @Valid @ValidContactParam String searchType,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        log.info(
                "Search contact by {} with param = {} page = {} pageSize = {}", searchType, searchParam, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        switch (ContactParamEnum.fromValue(searchType)) {
            case ContactParamEnum.IDENTIFIER:
                return contactService.searchContactByIdentifier(searchParam.toUpperCase(), pageable);
            case ContactParamEnum.NAME:
                return contactService.searchContactByName(searchParam.toUpperCase(), pageable);
            case ContactParamEnum.EMAIL:
                return contactService.searchContactByEmail(searchParam.toUpperCase(), pageable);
        }
        return new PageImpl<>(Collections.emptyList());

    }

    @GetMapping(path = Constants.API_CONTACTS_SEARCH+"/V2", produces = "application/json")
    @Operation(summary = "Search contact by parameter (identifier, email, name, firstName lastName)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SearchContactDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public Page<SearchContactDto> searchContactByParam(
            @RequestParam(required = true) String searchParam,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        log.info(
                "Search contact with param = {} page = {} pageSize = {}", searchParam, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        return contactService.searchContactByParam(searchParam.toUpperCase(), pageable);

    }


    static class ContactPage extends PageImpl<ContactDto> {

        @Serial
        private static final long serialVersionUID = 656181199902518234L;

        public ContactPage(List<ContactDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
