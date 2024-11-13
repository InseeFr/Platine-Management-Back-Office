package fr.insee.survey.datacollectionmanagement.questioning.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SurveyUnitCommentOutputDto {
    private String comment;
    private String author;
    private Date commentDate;
}
