package fr.insee.survey.datacollectionmanagement.user.dto;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.user.validation.UserEventTypeValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserEventDto {

    private Long id;
    @NotBlank(message = "identifier can't be blank")
    private String identifier;
        private Instant eventDate;
    @UserEventTypeValid
    private String type;
    private JsonNode payload;

}
