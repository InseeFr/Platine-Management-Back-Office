package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.ImpossibleToDeleteException;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDetailsDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitParamEnum;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.questioning.validation.ValidSurveyUnitParam;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "2 - Questioning", description = "Enpoints to create, update, delete and find entities around the questionings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SurveyUnitController {

    private final SurveyUnitService surveyUnitService;
    private final ViewService viewService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Search for a survey units, paginated")
    @GetMapping(value = Constants.API_SURVEY_UNITS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SurveyUnitPage.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @Deprecated(since="2.6.0", forRemoval=true)
    public Page<SurveyUnitDto> getSurveyUnits(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "idSu") String sort) {
        log.warn("DEPRECATED");
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<SurveyUnit> pageC = surveyUnitService.findAll(pageable);
        List<SurveyUnitDto> listSuDto = pageC.stream().map(this::convertToDto).toList();
        return new SurveyUnitPage(listSuDto, pageable, pageC.getTotalElements());
    }

    @Operation(summary = "Multi-criteria search survey-unit")
    @GetMapping(value = Constants.API_SURVEY_UNITS_SEARCH, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SearchSurveyUnitDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public Page<SearchSurveyUnitDto> searchSurveyUnits(
            @RequestParam(required = true) String searchParam,
            @RequestParam(required = true) @Valid @ValidSurveyUnitParam @Schema(description = "id or code or name")String searchType,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info(
                "Search surveyUnit by {} with param = {} page = {} pageSize = {}", searchType, searchParam, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        return switch (SurveyUnitParamEnum.fromValue(searchType)) {
            case SurveyUnitParamEnum.IDENTIFIER -> surveyUnitService.findbyIdentifier(searchParam.toUpperCase(), pageable);
            case SurveyUnitParamEnum.CODE -> surveyUnitService.findbyIdentificationCode(searchParam.toUpperCase(), pageable);
            case SurveyUnitParamEnum.NAME -> surveyUnitService.findbyIdentificationName(searchParam.toUpperCase(), pageable);
        };
    }

    @Operation(summary = "Search for a survey unit by its id")
    @GetMapping(value = Constants.API_SURVEY_UNITS_ID, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SurveyUnitDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public SurveyUnitDetailsDto findSurveyUnit(@PathVariable("id") String id) {
        SurveyUnit surveyUnit = surveyUnitService.findbyId(id);
        SurveyUnitDetailsDto surveyUnitDetailsDto =  convertToDetailsDto(surveyUnit);
        surveyUnitDetailsDto.setHasQuestionings(!viewService.findViewByIdSu(id).isEmpty());
        return surveyUnitDetailsDto;

    }

    @Operation(summary = "Multi-criteria search survey-unit")
    @GetMapping(value = Constants.API_SURVEY_UNITS_SEARCH+"/V2", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SearchSurveyUnitDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public Page<SearchSurveyUnitDto> searchSurveyUnitsByParam(
            @RequestParam(required = true) String searchParam,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info(
                "Search surveyUnit ith param = {} page = {} pageSize = {}", searchParam, page, pageSize);

        Pageable pageable = PageRequest.of(page, pageSize);

        return surveyUnitService.findByParameter(searchParam, pageable);
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
    @Deprecated(since="2.6.0", forRemoval=true)
    public void deleteSurveyUnit(@PathVariable("id") String id) {
        log.warn("DEPRECATED");
        SurveyUnit surveyUnit = surveyUnitService.findbyId(id);

        if (!surveyUnit.getQuestionings().isEmpty()) {
            log.warn("Some questionings exist for the survey unit {}, the survey unit can't be deleted", id);
            throw new ImpossibleToDeleteException("Some questionings exist for this survey unit, the survey unit can't be deleted");

        }
        surveyUnitService.deleteSurveyUnit(id);

    }

    private SurveyUnitDto convertToDto(SurveyUnit surveyUnit) {
        return modelMapper.map(surveyUnit, SurveyUnitDto.class);
    }

    private SurveyUnitDetailsDto convertToDetailsDto(SurveyUnit surveyUnit) {
        return modelMapper.map(surveyUnit, SurveyUnitDetailsDto.class);
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
