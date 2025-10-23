package fr.insee.survey.datacollectionmanagement.questioning.domain;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitEventSource;
import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitEventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SurveyUnitEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "survey_unit_event_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_unit_id")
    @NotNull
    private SurveyUnit surveyUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    @NotNull
    private Campaign campaign;

    @Column(name= "date")
    @NotNull
    private LocalDateTime date;

    @Column(name= "creation_date")
    @NotNull
    private LocalDateTime creationDate;

    @Column(name= "type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private SurveyUnitEventType type;

    @Column(name= "source")
    @Enumerated(EnumType.STRING)
    @NotNull
    private SurveyUnitEventSource source;

    public SurveyUnitEvent(SurveyUnit surveyUnit,
                           Campaign campaign,
                           LocalDateTime date,
                           LocalDateTime creationDate,
                           SurveyUnitEventType type,
                           SurveyUnitEventSource source) {
        this.surveyUnit = surveyUnit;
        this.campaign = campaign;
        this.date = date;
        this.creationDate = creationDate;
        this.type = type;
        this.source = source;
    }
}
