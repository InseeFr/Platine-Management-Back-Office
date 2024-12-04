package fr.insee.survey.datacollectionmanagement.contact.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactEventDto;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController(value = "contactEvents")
@PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
@Tag(name = "1 - Contacts", description = "Enpoints to create, update, delete and find contacts")
@RequiredArgsConstructor
@Validated
public class ContactEventController {

    private final ContactEventService contactEventService;

    private final ContactService contactService;

    private final ModelMapper modelMapper;

    /**
     * @deprecated
     */
    @Operation(summary = "Search for contactEvents by the contact id")
    @GetMapping(value = Constants.API_CONTACTS_ID_CONTACTEVENTS, produces = "application/json")
    @Deprecated(since = "2.6.0", forRemoval = true)
    public ResponseEntity<List<ContactEventDto>> getContactContactEvents(@PathVariable("id") String identifier) {
        Contact contact = contactService.findByIdentifier(identifier);
        return ResponseEntity.status(HttpStatus.OK)
                .body(contact.getContactEvents().stream().map(this::convertToDto)
                        .toList());


    }


    /**
     * @deprecated
     */
    @Operation(summary = "Create a contactEvent")
    @PostMapping(value = Constants.API_CONTACTEVENTS, produces = "application/json", consumes = "application/json")
    @Deprecated(since = "2.6.0", forRemoval = true)
    public ResponseEntity<ContactEventDto> postContactEvent(@RequestBody @Valid ContactEventDto contactEventDto) {

        contactService.findByIdentifier(contactEventDto.getIdentifier());
        ContactEvent contactEvent = convertToEntity(contactEventDto);
        ContactEvent newContactEvent = contactEventService.saveContactEvent(contactEvent);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders)
                .body(convertToDto(newContactEvent));

    }



    /**
     * @deprecated
     */
    @Operation(summary = "Delete a contact event")
    @DeleteMapping(value = Constants.API_CONTACTEVENTS_ID, produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated(since = "2.6.0", forRemoval = true)
    public void deleteContactEvent(@PathVariable("id") Long id) {
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
