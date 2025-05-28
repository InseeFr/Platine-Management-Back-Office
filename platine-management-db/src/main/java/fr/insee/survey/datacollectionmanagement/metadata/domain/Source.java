package fr.insee.survey.datacollectionmanagement.metadata.domain;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.source.enums.SourceType;
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
    private String storageTime;
    private String personalData;
    @NonNull
    @Enumerated(EnumType.STRING)
    private PeriodicityEnum periodicity;
    @NonNull
    private Boolean mandatoryMySurveys;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "source")
    private Set<Survey> surveys;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "source")
    private Set<SourceAccreditation> sourceAccreditations;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "support_id")
    private Support support;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;

    private SourceType type;
}
