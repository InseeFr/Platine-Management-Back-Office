package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idPartitioning_index", columnList = "idPartitioning"),
        @Index(name = "surveyUnitId_index", columnList = "survey_unit_id_su")

})
public class Questioning {

    @Id
    private UUID id;

    @NonNull
    private String modelName;
    @NonNull
    private String idPartitioning;

    private String assistanceMail;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "questioning")
    private Set<QuestioningAccreditation> questioningAccreditations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "questioning")
    private Set<QuestioningEvent> questioningEvents;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "questioning")
    private Set<QuestioningCommunication> questioningCommunications;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "questioning")

    private Set<QuestioningComment> questioningComments;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_unit_id_su")
    @NonNull
    private SurveyUnit surveyUnit;

}
