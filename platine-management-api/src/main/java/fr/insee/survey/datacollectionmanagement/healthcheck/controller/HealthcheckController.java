package fr.insee.survey.datacollectionmanagement.healthcheck.controller;

import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.healthcheck.dto.HealthcheckDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "9 - Healthcheck", description = "healthcheck")
public class HealthcheckController {

    @GetMapping(path = UrlConstants.API_HEALTHCHECK, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HealthcheckDto> healthcheck() {
        HealthcheckDto dto = new HealthcheckDto();
        dto.setStatus("OK");
        return ResponseEntity.ok().body(dto);
    }
}
