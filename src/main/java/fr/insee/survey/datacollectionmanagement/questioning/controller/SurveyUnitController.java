package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@PreAuthorize("hasRole('INTERNAL_USER') "
        + "|| hasRole('WEB_CLIENT') "
        + "|| hasRole('ADMIN') ")
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SurveyUnitController {

    private final SurveyUnitService surveyUnitService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Search for a survey units, paginated")
    @GetMapping(value = Constants.API_SURVEY_UNITS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SurveyUnitPage.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public Page<SurveyUnitDto> getSurveyUnits(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "idSu") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<SurveyUnit> pageC = surveyUnitService.findAll(pageable);
        List<SurveyUnitDto> listSuDto = pageC.stream().map(this::convertToDto).toList();
        return new SurveyUnitPage(listSuDto, pageable, pageC.getTotalElements());
    }

    @Operation(summary = "Multi-criteria search survey-unit")
    @GetMapping(value = Constants.API_SURVEY_UNITS_SEARCH, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SurveyUnitPage.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public Page<SurveyUnitDto> searchSurveyUnits(
            @RequestParam(required = false) String idSu,
            @RequestParam(required = false) String identificationCode,
            @RequestParam(required = false) String identificationName,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id_su") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<SurveyUnit> pageC = surveyUnitService.findByParameters(idSu, identificationCode, identificationName, pageable);
        List<SurveyUnitDto> listSuDto = pageC.stream().map(this::convertToDto).toList();
        return new SurveyUnitPage(listSuDto, pageable, pageC.getTotalElements());
    }

    @Operation(summary = "Search for a survey unit by its id")
    @GetMapping(value = Constants.API_SURVEY_UNITS_ID, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SurveyUnitDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<SurveyUnitDto> findSurveyUnit(@PathVariable("id") String id) {
        SurveyUnit surveyUnit = surveyUnitService.findbyId(StringUtils.upperCase(id));
        return ResponseEntity.status(HttpStatus.OK).body(convertToDto(surveyUnit));

    }

    @Operation(summary = "Create or update a survey unit")
    @PutMapping(value = Constants.API_SURVEY_UNITS_ID, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SurveyUnitDto.class))),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = SurveyUnitDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<SurveyUnitDto> putSurveyUnit(@PathVariable("id") String id, @RequestBody @Valid SurveyUnitDto surveyUnitDto) {
        if (!surveyUnitDto.getIdSu().equalsIgnoreCase(id)) {
            throw new NotMatchException("id and idSu don't match");
        }

        SurveyUnit surveyUnit;
        HttpStatus responseStatus;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(surveyUnitDto.getIdSu()).toUriString());

        surveyUnit = convertToEntity(surveyUnitDto);

        try {
            surveyUnitService.findbyId(surveyUnitDto.getIdSu());
            responseStatus = HttpStatus.OK;

        } catch (NotFoundException e) {
            log.info("Creating survey with the id {}", surveyUnitDto.getIdSu());
            responseStatus = HttpStatus.CREATED;
        }

        return ResponseEntity.status(responseStatus)
                .body(convertToDto(surveyUnitService.saveSurveyUnitAndAddress(surveyUnit)));

    }

    @Operation(summary = "Delete a survey unit by its id")
    @DeleteMapping(value = Constants.API_SURVEY_UNITS_ID, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<String> deleteSurveyUnit(@PathVariable("id") String id) {
        SurveyUnit surveyUnit = surveyUnitService.findbyId(StringUtils.upperCase(id));

        try {
            if (!surveyUnit.getQuestionings().isEmpty()) {
                log.warn("Some questionings exist for the survey unit {}, the survey unit can't be deleted", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Some questionings exist for this survey unit, the survey unit can't be deleted");
            }
            surveyUnitService.deleteSurveyUnit(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Survey unit deleted");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }

    private SurveyUnitDto convertToDto(SurveyUnit surveyUnit) {
        return modelMapper.map(surveyUnit, SurveyUnitDto.class);
    }

    private SurveyUnit convertToEntity(SurveyUnitDto surveyUnitDto) {
        return modelMapper.map(surveyUnitDto, SurveyUnit.class);
    }

    class SurveyUnitPage extends PageImpl<SurveyUnitDto> {

        private static final long serialVersionUID = 656181199902518234L;

        public SurveyUnitPage(List<SurveyUnitDto> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}
