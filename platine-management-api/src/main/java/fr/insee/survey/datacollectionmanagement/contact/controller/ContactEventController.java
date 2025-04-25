package fr.insee.survey.datacollectionmanagement.contact.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactEventDto;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController(value = "contactEvents")
@PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
@Tag(name = "1 - Contacts", description = "Enpoints to create, update, delete and find contacts")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ContactEventController {

    private final ContactEventService contactEventService;

    private final ContactService contactService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Create a contact event")
    @PostMapping(value = UrlConstants.API_CONTACT_CONTACTEVENTS, produces = "application/json", consumes = "application/json")
    @PreAuthorize(AuthorityPrivileges.HAS_RESPONDENT_PRIVILEGES)
    public ResponseEntity<ContactEventDto> postContactEvent(@RequestBody @Valid ContactEventDto contactEventDto,
                                                               @CurrentSecurityContext(expression = "authentication.name") String contactId) {
        if (!contactEventDto.getIdentifier().equalsIgnoreCase(contactId)) {
            throw new NotMatchException("contactId and contact identifier don't match");
        }
        if (!contactService.existsByIdentifier(contactId.toUpperCase())) {
            throw new NotFoundException(String.format("contact %s not found", contactId.toUpperCase()));
        }
        ContactEventDto newContactEvent = contactEventService.addContactEvent(contactEventDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(newContactEvent);
    }

    @Operation(summary = "Create a contactEvent (accessible only by the 'platine-service' service account)")
    @PostMapping(value = UrlConstants.API_CONTACT_CONTACTEVENTS_PLATINEACCOUNT, produces = "application/json", consumes = "application/json")
    @PreAuthorize("authentication.principal.username == 'platine-service'")
    public ResponseEntity<ContactEventDto> postContactEventWithPlatineServiceAccount(@RequestBody @Valid ContactEventDto contactEventDto) {

        if (!contactService.existsByIdentifier(contactEventDto.getIdentifier().toUpperCase())) {
            throw new NotFoundException(String.format("contact %s not found", contactEventDto.getIdentifier().toUpperCase()));
        }

        ContactEventDto newContactEvent = contactEventService.addContactEvent(contactEventDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(newContactEvent);
    }

    @Operation(summary = "Find all contact-events")
    @GetMapping(value = UrlConstants.API_CONTACT_CONTACTEVENTS, produces = "application/json")
    @PreAuthorize(AuthorityPrivileges.HAS_RESPONDENT_PRIVILEGES)
    public List<ContactEventDto> getAllContactEvents(@CurrentSecurityContext(expression = "authentication.name") String contactId) {
        if (!contactService.existsByIdentifier(contactId.toUpperCase())) {
            throw new NotFoundException(String.format("contact %s not found", contactId.toUpperCase()));
        }
        return contactEventService.findContactEventsByContactId(contactId.toUpperCase());
    }

    /**
     * @deprecated
     */
    @Operation(summary = "Search for contactEvents by the contact id")
    @GetMapping(value = UrlConstants.API_CONTACTS_ID_CONTACTEVENTS, produces = "application/json")
    @Deprecated(since = "2.6.0", forRemoval = true)
    public ResponseEntity<List<ContactEventDto>> getContactContactEvents(@PathVariable("id") String identifier) {
        log.warn("DEPRECATED");
        Contact contact = contactService.findByIdentifier(identifier);
        return ResponseEntity.status(HttpStatus.OK)
                .body(contact.getContactEvents().stream().map(this::convertToDto)
                        .toList());
    }



    /**
     * @deprecated
     */
    @Operation(summary = "Delete a contact event")
    @DeleteMapping(value = UrlConstants.API_CONTACTEVENTS_ID, produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated(since = "2.6.0", forRemoval = true)
    public void deleteContactEvent(@PathVariable("id") Long id) {
        log.warn("DEPRECATED");
        contactEventService.findById(id);
        contactEventService.deleteContactEvent(id);

    }

    private ContactEventDto convertToDto(ContactEvent contactEvent) {
        ContactEventDto ceDto = modelMapper.map(contactEvent, ContactEventDto.class);
        ceDto.setIdentifier(contactEvent.getContact().getIdentifier());
        return ceDto;
    }

    private ContactEvent convertToEntity(ContactEventDto contactEventDto) {
         return modelMapper.map(contactEventDto, ContactEvent.class);
    }


}
