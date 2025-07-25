package fr.insee.survey.datacollectionmanagement.contact.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.contact.util.PayloadUtil;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "1 - Contacts", description = "Enpoints to create, update, delete and find contacts")
@Slf4j
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    private final ContactService contactService;

    private final ContactEventService contactEventService;

    /**
     * @deprecated
     */
    @Operation(summary = "Search for a contact address by the contact id")
    @GetMapping(value = UrlConstants.API_CONTACTS_ID_ADDRESS, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || " + AuthorityPrivileges.HAS_RESPONDENT_LIMITED_PRIVILEGES)
    @Deprecated(since="2.6.0", forRemoval=true)
    public ResponseEntity<AddressDto> getContactAddress(@PathVariable("id") String id) {
        log.warn("DEPRECATED");
        Contact contact = contactService.findByIdentifier(id);
        if (contact.getAddress() != null)
            return ResponseEntity.status(HttpStatus.OK)
                    .body(addressService.convertToDto(contact.getAddress()));
        else throw new NotFoundException(String.format("No address found for contact %s", id));


    }

    @Operation(summary = "Update or create an address by the contact id")
    @PutMapping(value = UrlConstants.API_CONTACTS_ID_ADDRESS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || " + AuthorityPrivileges.HAS_RESPONDENT_LIMITED_PRIVILEGES)
    public ResponseEntity<AddressDto> putAddress(@PathVariable("id") String id,
                                                 @RequestBody AddressDto addressDto,
                                                 Authentication auth) {
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
        PayloadUtil.getPayloadAuthor(auth.getName());
        ContactEvent contactEventUpdate = contactEventService.createContactEvent(contact, ContactEventTypeEnum.update,
                null);
        contactEventService.saveContactEvent(contactEventUpdate);
        return ResponseEntity.status(httpStatus).headers(responseHeaders)
                .body(addressService.convertToDto(addressUpdate));


    }

}
