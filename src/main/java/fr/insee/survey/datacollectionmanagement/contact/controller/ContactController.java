package fr.insee.survey.datacollectionmanagement.contact.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent.ContactEventType;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactFirstLoginDto;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.contact.util.PayloadUtil;
import fr.insee.survey.datacollectionmanagement.exception.ImpossibleToDeleteException;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
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
import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "1 - Contacts", description = "Endpoints to create, update, delete and find contacts")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ContactController {

    private final ContactService contactService;

    private final AddressService addressService;

    private final ViewService viewService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Search for contacts, paginated")
    @GetMapping(value = Constants.API_CONTACTS_ALL, produces = "application/json")
    public ResponseEntity<ContactPage> getContacts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "identifier") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Contact> pageC = contactService.findAll(pageable);
        List<ContactDto> listC = pageC.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(new ContactPage(listC, pageable, pageC.getTotalElements()));
    }

    @Operation(summary = "Search for a contact by its id")
    @GetMapping(value = Constants.API_CONTACTS_ID)
    @PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES + " || " + AuthorityPrivileges.HAS_REPONDENT_LIMITATED_PRIVILEGES)
    public ResponseEntity<ContactFirstLoginDto> getContact(@PathVariable("id") String id) {
        Contact contact = contactService.findByIdentifier(StringUtils.upperCase(id));
        return ResponseEntity.ok().body(convertToFirstLoginDto(contact));


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
        Contact contact;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION, ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(contactDto.getIdentifier()).toUriString());

        JsonNode payload = PayloadUtil.getPayloadAuthor(auth.getName());

        try {
            contact = convertToEntity(contactDto);
            if (contactDto.getAddress() != null)
                contact.setAddress(addressService.convertToEntity(contactDto.getAddress()));
            Contact contactUpdate = contactService.updateContactAddressEvent(contact, payload);
            return ResponseEntity.ok().headers(responseHeaders).body(convertToDto(contactUpdate));
        } catch (NotFoundException e) {
            log.info("Creating contact with the identifier {}", contactDto.getIdentifier());
            contact = convertToEntityNewContact(contactDto);
            if (contactDto.getAddress() != null)
                contact.setAddress(addressService.convertToEntity(contactDto.getAddress()));
            Contact contactCreate = contactService.createContactAddressEvent(contact, payload);
            viewService.createView(id, null, null);
            return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders).body(convertToDto(contactCreate));
        }

    }


    @Operation(summary = "Delete a contact, its address, its contactEvents")
    @DeleteMapping(value = Constants.API_CONTACTS_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(@PathVariable("id") String id) {

        if (!questioningAccreditationService.findByContactIdentifier(id).isEmpty()) {
            throw new ImpossibleToDeleteException(
                    String.format("Contact %s cannot be deleted as he/she is still entitled to answer one or more questionnaires", id));
        }

        log.info("Delete contact {}", id);
        Contact contact = contactService.findByIdentifier(id);
        contactService.deleteContactAddressEvent(contact);

    }

    private ContactDto convertToDto(Contact contact) {
        ContactDto contactDto = modelMapper.map(contact, ContactDto.class);
        contactDto.setCivility(contact.getGender().name());
        return contactDto;
    }

    private ContactFirstLoginDto convertToFirstLoginDto(Contact contact) {
        ContactFirstLoginDto contactFirstLoginDto = modelMapper.map(contact, ContactFirstLoginDto.class);
        contactFirstLoginDto.setCivility(contact.getGender());
        contactFirstLoginDto.setFirstConnect(contact.getContactEvents().stream().noneMatch(e -> e.getType().equals(ContactEventType.firstConnect)));
        return contactFirstLoginDto;
    }

    private Contact convertToEntity(ContactDto contactDto) {
        Contact contact = modelMapper.map(contactDto, Contact.class);
        contact.setGender(Contact.Gender.valueOf(contactDto.getCivility()));
        Contact oldContact = contactService.findByIdentifier(contactDto.getIdentifier());
        contact.setComment(oldContact.getComment());
        contact.setAddress(oldContact.getAddress());
        contact.setContactEvents(oldContact.getContactEvents());

        return contact;
    }

    private Contact convertToEntityNewContact(ContactDto contactDto) {
        Contact contact = modelMapper.map(contactDto, Contact.class);
        contact.setGender(Contact.Gender.valueOf(contactDto.getCivility()));
        return contact;
    }

    static class ContactPage extends PageImpl<ContactDto> {

        @Serial
        private static final long serialVersionUID = 656181199902518234L;

        public ContactPage(List<ContactDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
