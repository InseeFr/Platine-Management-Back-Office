package fr.insee.survey.datacollectionmanagement.metadata.domain;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.user.domain.SourceAccreditation;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Source {

    @Id
    private String id;
    private String longWording;
    private String shortWording;
    private Boolean forceClose;
    private String messageSurveyOffline;
    private String messageInfoSurveyOffline;
    private String logo;
    @NonNull
    @Enumerated(EnumType.STRING)
    private PeriodicityEnum periodicity;
    @NonNull
    private Boolean mandatoryMySurveys;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Survey> surveys;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<SourceAccreditation> sourceAccreditations;

    @ManyToOne
    @NonNull
    private Owner owner;

    @ManyToOne
    @NonNull
    private Support support;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;

}
