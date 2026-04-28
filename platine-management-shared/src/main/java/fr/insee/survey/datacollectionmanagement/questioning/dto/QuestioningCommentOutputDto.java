package fr.insee.survey.datacollectionmanagement.questioning.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class QuestioningCommentOutputDto {
    private String comment;
    private String author;
        private Instant commentDate;
}
