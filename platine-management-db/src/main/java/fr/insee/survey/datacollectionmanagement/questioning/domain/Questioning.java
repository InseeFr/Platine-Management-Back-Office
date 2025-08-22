package fr.insee.survey.datacollectionmanagement.questioning.domain;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
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
    private Set<QuestioningEvent> questioningEvents = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "questioning")
    private Set<QuestioningCommunication> questioningCommunications;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "questioning")
    private Set<QuestioningComment> questioningComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_unit_id_su")
    @NonNull
    private SurveyUnit surveyUnit;

    private Integer score;
    @Column(name= "score_init")
    private Integer scoreInit;

    @Column(name= "highest_event_date")
    private Date highestEventDate;

    @Column(name= "highest_event_type")
    @Enumerated(EnumType.STRING)
    private TypeQuestioningEvent highestEventType;

}
