package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idPartitioning_index", columnList = "idPartitioning"),
        @Index(name = "surveyUnitId_index", columnList = "survey_unit_id_su")

})
public class Questioning {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questioning_seq")
    private Long id;

    @NonNull
    private String modelName;
    @NonNull
    private String idPartitioning;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<QuestioningAccreditation> questioningAccreditations;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<QuestioningEvent> questioningEvents;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NonNull
    private SurveyUnit surveyUnit;

}
