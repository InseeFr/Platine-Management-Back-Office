package fr.insee.survey.datacollectionmanagement.contact.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent.ContactEventType;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@Tag(name = "1 - Contacts", description = "Enpoints to create, update, delete and find contacts")
@Slf4j
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    private final ContactService contactService;

    private final ContactEventService contactEventService;

    @Operation(summary = "Search for a contact address by the contact id")
    @GetMapping(value = Constants.API_CONTACTS_ID_ADDRESS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AddressDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal servor error")
    })
    @PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
            + "|| @AuthorizeMethodDecider.isWebClient() "
            + "|| (@AuthorizeMethodDecider.isRespondent() && (#id == @AuthorizeMethodDecider.getUsername()))"
            + "|| @AuthorizeMethodDecider.isAdmin() ")
    public ResponseEntity<?> getContactAddress(@PathVariable("id") String id) {
        Contact contact = contactService.findByIdentifier(id);
        try {
            if (contact.getAddress() != null)
                return ResponseEntity.status(HttpStatus.OK)
                        .body(addressService.convertToDto(contact.getAddress()));
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Address does not exist");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }

    }

    @Operation(summary = "Update or create an address by the contact id")
    @PutMapping(value = Constants.API_CONTACTS_ID_ADDRESS, produces = "application/json", consumes = "application/json")
    @PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
            + "|| @AuthorizeMethodDecider.isWebClient() "
            + "|| (@AuthorizeMethodDecider.isRespondent() && (#id == @AuthorizeMethodDecider.getUsername()))"
            + "|| @AuthorizeMethodDecider.isAdmin() ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AddressDto.class))),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ContactDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<?> putAddress(@PathVariable("id") String id, @RequestBody AddressDto addressDto) {
        Contact contact = contactService.findByIdentifier(id);
        HttpStatus httpStatus;
        Address addressUpdate;
        Address address = addressService.convertToEntity(addressDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION, ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        if (contact.getAddress() != null) {
            log.info("Update address for the contact {} ", id);
            address.setId(contact.getAddress().getId());
            addressUpdate = addressService.saveAddress(address);
            httpStatus = HttpStatus.OK;
        } else {
            log.info("Create address for the contact {} ", id);
            addressUpdate = addressService.saveAddress(address);
            contact.setAddress(addressUpdate);
            contactService.saveContact(contact);
            httpStatus = HttpStatus.CREATED;
        }

        ContactEvent contactEventUpdate = contactEventService.createContactEvent(contact, ContactEventType.update,
                null);
        contactEventService.saveContactEvent(contactEventUpdate);
        return ResponseEntity.status(httpStatus).headers(responseHeaders)
                .body(addressService.convertToDto(addressUpdate));


    }

    class AddressPage extends PageImpl<AddressDto> {

        private static final long serialVersionUID = -5570255373624396569L;

        public AddressPage(List<AddressDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
