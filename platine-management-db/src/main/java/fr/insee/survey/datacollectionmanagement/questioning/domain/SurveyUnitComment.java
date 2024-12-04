package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class SurveyUnitComment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "su_comment_seq")
    private Long id;
    @Column(length = 2000)
    private String comment;
    private String author;
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_unit_id_su")
    private SurveyUnit surveyUnit;
}
