package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Owner;
import fr.insee.survey.datacollectionmanagement.metadata.dto.OwnerDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "4 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
@RequiredArgsConstructor
@Validated
public class OwnerController {

    private final ModelMapper modelmapper;

    private final OwnerService ownerService;

    @Operation(summary = "Search for owners, paginated")
    @GetMapping(value = UrlConstants.API_OWNERS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OwnerPage> getOwners(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Owner> pageOwner = ownerService.findAll(pageable);
        List<OwnerDto> listOwners = pageOwner.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(new OwnerPage(listOwners, pageable, pageOwner.getTotalElements()));
    }

    @Operation(summary = "Search for a owner by its id")
    @GetMapping(value = UrlConstants.API_OWNERS_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OwnerDto> getOwner(@PathVariable("id") String id) {
        Owner owner = ownerService.findById(id);
        return ResponseEntity.ok().body(convertToDto(owner));

    }

    @Operation(summary = "Update or create a owner")
    @PutMapping(value = UrlConstants.API_OWNERS_ID, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public ResponseEntity<OwnerDto> putOwner(@PathVariable("id") String id, @RequestBody @Valid OwnerDto ownerDto) {
        if (!ownerDto.getId().equals(id)) {
            throw new NotMatchException("id and owner id don't match");
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(ownerDto.getId()).toUriString());
        HttpStatus httpStatus;

        try {
            Owner ownerBase = ownerService.findById(id);
            log.warn("Update owner with the id {}", ownerDto.getId());
            httpStatus = HttpStatus.OK;

        } catch (NotFoundException e) {
            log.info("Create owner with the id {}", ownerDto.getId());
            httpStatus = HttpStatus.CREATED;
        }


        Owner owner = ownerService.insertOrUpdateOwner(convertToEntity(ownerDto));
        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(convertToDto(owner));
    }


    private OwnerDto convertToDto(Owner owner) {
        return modelmapper.map(owner, OwnerDto.class);
    }

    private Owner convertToEntity(OwnerDto ownerDto) {
        return modelmapper.map(ownerDto, Owner.class);
    }

    class OwnerPage extends PageImpl<OwnerDto> {

        public OwnerPage(List<OwnerDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }

}
