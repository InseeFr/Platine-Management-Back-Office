package fr.insee.survey.datacollectionmanagement.questioning.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QuestioningAccreditationDto {

    @JsonIgnore
    private Long id;

    private boolean isMain;
    private Date creationDate;
    private String creationAuthor;
    private String idContact;

}
