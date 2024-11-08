package fr.insee.survey.datacollectionmanagement.user.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SourceAccreditationDto {

    @JsonIgnore
    private Long id;
    private Date creationDate;
    @NotBlank(message = "creationAuthor can't be empty")
    private String creationAuthor;
    @NotBlank(message = "idUser can't be empty")
    private String idUser;

}
