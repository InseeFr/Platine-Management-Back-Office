package fr.insee.survey.datacollectionmanagement.questioning.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QuestioningCommentOutputDto {
    private String comment;
    private String author;
    private Date commentDate;
}
