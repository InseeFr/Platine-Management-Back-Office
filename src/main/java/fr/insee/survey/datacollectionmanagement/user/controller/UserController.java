package fr.insee.survey.datacollectionmanagement.user.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import fr.insee.survey.datacollectionmanagement.user.domain.SourceAccreditation;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.dto.UserDto;
import fr.insee.survey.datacollectionmanagement.user.service.SourceAccreditationService;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

@RestController
@PreAuthorize("hasRole('INTERNAL_USER') "
        + "|| hasRole('WEB_CLIENT')" +
        " || hasRole('ADMIN') ")
@Tag(name = "7-User", description = "Enpoints to create, update, delete and find users, their events and accreditations")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final SourceService sourceService;

    private final SourceAccreditationService sourceAccreditationService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Search for users, paginated")
    @GetMapping(value = Constants.API_USERS_ALL, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserController.UserPage.class)))
    })
    public ResponseEntity getUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "identifier") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<User> pageC = userService.findAll(pageable);
        List<UserDto> listC = pageC.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(new UserController.UserPage(listC, pageable, pageC.getTotalElements()));
    }

    @Operation(summary = "Search for users, without pagination")
    @GetMapping(value = Constants.API_USERS_ALL_NO_PAGINATION, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class ))))
    })
    public ResponseEntity getUsersNotPaginated() {
        List<User> users = userService.findAll();
        List<UserDto> listUsers = users.stream().map(this::convertToDto).toList();
        return ResponseEntity.ok().body(listUsers);
    }

    @Operation(summary = "Search for a user by its id")
    @GetMapping(value = Constants.API_USERS_ID, produces = "application/json")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") String id) {
        User user = userService.findByIdentifier(id);
        return ResponseEntity.ok().body(convertToDto(user));


    }

    @Operation(summary = "Update or create user")
    @PutMapping(value = Constants.API_USERS_ID, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity putUser(@PathVariable("id") String id, @Valid @RequestBody UserDto userDto) {
        if (!userDto.getIdentifier().equalsIgnoreCase(id)) {
            throw new NotMatchException("id and user identifier don't match");
        }
        User user;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION, ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(userDto.getIdentifier()).toUriString());

        try {
            user = convertToEntity(userDto);
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossible to parse user");

        } catch (NotFoundException e) {
            log.info("Creating user with the identifier {}", userDto.getIdentifier());
            user = convertToEntityNewUser(userDto);

            User userCreate = userService.createUser(user, null);
            return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders).body(convertToDto(userCreate));
        }

        log.info("Updating user with the identifier {}", userDto.getIdentifier());
        User userUpdate = userService.updateUser(user, null);

        return ResponseEntity.ok().headers(responseHeaders).body(convertToDto(userUpdate));
    }


    @Operation(summary = "Delete a user, its userEvents and its sourceaccreditations")
    @DeleteMapping(value = Constants.API_USERS_ID)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @Transactional
    public ResponseEntity deleteUser(@PathVariable("id") String id) {
        User user = userService.findByIdentifier(id);

        try {
            userService.deleteUserAndEvents(user);

            sourceAccreditationService.findByUserIdentifier(id).stream().forEach(acc -> {
                Source source = sourceService.findById(acc.getSource().getId());
                Set<SourceAccreditation> newSet = source.getSourceAccreditations();
                newSet.removeIf(a -> a.getId().equals(acc.getId()));
                source.setSourceAccreditations(newSet);
                sourceService.insertOrUpdateSource(source);
                sourceAccreditationService.deleteAccreditation(acc);

            });
            log.info("Delete user {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }

    @Operation(summary = "Get user accredited sources")
    @GetMapping(value = Constants.API_USERS_ID_SOURCES)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity getUserSources(@PathVariable("id") String id) {
        userService.findByIdentifier(id);
        List<String> accreditedSources = userService.findAccreditedSources(id);
        return ResponseEntity.status(HttpStatus.OK).body(accreditedSources);
    }


    private User convertToEntity(UserDto userDto) throws ParseException {

        User oldUser = userService.findByIdentifier(userDto.getIdentifier());
        User user = modelMapper.map(userDto, User.class);
        user.setRole(User.UserRoleType.valueOf(userDto.getRole()));
        user.setUserEvents(oldUser.getUserEvents());

        return user;
    }

    private User convertToEntityNewUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setRole(User.UserRoleType.valueOf(userDto.getRole()));
        return user;
    }

    private UserDto convertToDto(User user) {

        List<String> accreditedSources = userService.findAccreditedSources(user.getIdentifier());
        UserDto userDto= modelMapper.map(user, UserDto.class);
        userDto.setAccreditedSources(accreditedSources);
        return userDto;
    }

    class UserPage extends PageImpl<UserDto> {

        private static final long serialVersionUID = 656181199902518234L;

        public UserPage(List<UserDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}
