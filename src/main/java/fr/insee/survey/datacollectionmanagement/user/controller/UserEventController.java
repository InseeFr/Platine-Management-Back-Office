package fr.insee.survey.datacollectionmanagement.user.controller;


import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.domain.UserEvent;
import fr.insee.survey.datacollectionmanagement.user.dto.UserEventDto;
import fr.insee.survey.datacollectionmanagement.user.service.UserEventService;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
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
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;
import java.util.stream.Collectors;

@RestController(value = "UserEvents")
@PreAuthorize("hasRole('INTERNAL_USER') "
        + "|| hasRole('WEB_CLIENT') "
        + "|| hasRole('ADMIN') ")
@Slf4j
@Tag(name = "7-User", description = "Enpoints to create, update, delete and find users, their events and accreditations")
@RequiredArgsConstructor
@Validated
public class UserEventController {

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final UserEventService userEventService;

    @Operation(summary = "Search for userEvents by user's id")
    @GetMapping(value = Constants.API_USERS_ID_USEREVENTS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserEventDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Internal servor error")
    })
    public ResponseEntity getUserUserEvents(@PathVariable("id") String identifier) {
        User user = userService.findByIdentifier(identifier);

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(user.getUserEvents().stream().map(this::convertToDto)
                            .toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }

    }

    @Operation(summary = "Create a userEvent")
    @PostMapping(value = Constants.API_USEREVENTS, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = UserEventDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity postUserEvent(@Valid @RequestBody UserEventDto userEventDto) {

        User user = userService.findByIdentifier(userEventDto.getIdentifier());
        UserEvent userEvent = convertToEntity(userEventDto);
        UserEvent newUserEvent = userEventService.saveUserEvent(userEvent);
        Set<UserEvent> setUserEvents = user.getUserEvents();
        setUserEvents.add(newUserEvent);
        user.setUserEvents(setUserEvents);
        userService.saveUser(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders)
                .body(convertToDto(newUserEvent));


    }

    @Operation(summary = "Delete a contact event")
    @DeleteMapping(value = Constants.API_USEREVENTS_ID, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity deleteUserEvent(@PathVariable("id") Long id) {
        UserEvent userEvent = userEventService.findById(id);

        try {
            User user = userEvent.getUser();
            user.setUserEvents(user.getUserEvents().stream().filter(ue -> !ue.equals(userEvent))
                    .collect(Collectors.toSet()));
            userService.saveUser(user);
            userEventService.deleteUserEvent(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User event deleted");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");

        }
    }

    private UserEventDto convertToDto(UserEvent userEvent) {
        UserEventDto ueDto = modelMapper.map(userEvent, UserEventDto.class);
        ueDto.setIdentifier(userEvent.getUser().getIdentifier());
        return ueDto;
    }

    private UserEvent convertToEntity(UserEventDto userEventDto) {
        return modelMapper.map(userEventDto, UserEvent.class);
    }
}
