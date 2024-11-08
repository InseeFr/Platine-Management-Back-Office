package fr.insee.survey.datacollectionmanagement.healthcheck.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.healthcheck.dto.HealthcheckDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "8 - Healthcheck", description = "healthcheck")
public class HealthcheckController {

    @GetMapping(path = Constants.API_HEALTHCHECK, produces = "application/json")
    public ResponseEntity<HealthcheckDto> healthcheck() {
        HealthcheckDto dto = new HealthcheckDto();
        dto.setStatus("OK");
        return ResponseEntity.ok().body(dto);
    }
}
