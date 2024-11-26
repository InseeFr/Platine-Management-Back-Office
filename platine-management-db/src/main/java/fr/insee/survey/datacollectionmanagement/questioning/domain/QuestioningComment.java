package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class QuestioningComment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quest_comment_seq")
    private Long id;
    @Column(length = 2000)
    private String comment;
    private String author;
    private Date date;
}
