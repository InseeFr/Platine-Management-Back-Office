package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idQuestioningComment_index", columnList = "questioning_id")
})
public class QuestioningComment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quest_comment_seq")
    private Long id;
    @Column(length = 2000)
    private String comment;
    private String author;
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questioning_id")
    private Questioning questioning;
}
