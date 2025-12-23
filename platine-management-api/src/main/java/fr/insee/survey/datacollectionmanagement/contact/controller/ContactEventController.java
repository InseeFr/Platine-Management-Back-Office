package fr.insee.survey.datacollectionmanagement.contact.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactEventDto;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value = "contactEvents")
@Tag(name = "1 - Contacts", description = "Enpoints to create, update, delete and find contacts")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ContactEventController {

    private final ContactEventService contactEventService;

    private final ContactService contactService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Create a contactEvent (accessible only by user with PORTAL_PRIVILEGE)")
    @PostMapping(value = UrlConstants.API_CONTACT_CONTACTEVENTS_PORTAL_PRIVILEGE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_PORTAL_PRIVILEGES)
    public ResponseEntity<ContactEventDto> postContactEventWithPlatineServiceAccount(@RequestBody @Valid ContactEventDto contactEventDto) {

        if (!contactService.existsByIdentifier(contactEventDto.getIdentifier().toUpperCase())) {
            throw new NotFoundException(String.format("contact %s not found", contactEventDto.getIdentifier()));
        }

        ContactEventDto newContactEvent = contactEventService.addContactEvent(contactEventDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(newContactEvent);
    }

    @Operation(summary = "Find all contact-events")
    @GetMapping(value = UrlConstants.API_CONTACT_CONTACTEVENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_RESPONDENT_PRIVILEGES)
    public List<ContactEventDto> getAllContactEvents(@CurrentSecurityContext(expression = "authentication.name") String contactId) {
        if (!contactService.existsByIdentifier(contactId.toUpperCase())) {
            throw new NotFoundException(String.format("contact %s not found", contactId.toUpperCase()));
        }
        return contactEventService.findContactEventsByContactId(contactId.toUpperCase());
    }

    @Operation(summary = "Search for contactEvents by the contact id")
    @GetMapping(value = UrlConstants.API_CONTACTS_ID_CONTACTEVENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES + " || hasPermission(null, 'READ_SUPPORT')")

    public ResponseEntity<List<ContactEventDto>> getContactContactEvents(@PathVariable("id") String identifier) {
        Contact contact = contactService.findByIdentifier(identifier);
        return ResponseEntity.status(HttpStatus.OK)
                .body(contact.getContactEvents().stream().map(this::convertToDto)
                        .toList());
    }


    private ContactEventDto convertToDto(ContactEvent contactEvent) {
        ContactEventDto ceDto = modelMapper.map(contactEvent, ContactEventDto.class);
        ceDto.setIdentifier(contactEvent.getContact().getIdentifier());
        return ceDto;
    }

}
