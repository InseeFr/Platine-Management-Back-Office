package fr.insee.survey.datacollectionmanagement.metadata.domain;

import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "year_index", columnList = "year_value"),
        @Index(name = "surveyid_index", columnList = "survey_id")
})
public class Campaign {

    @Id
    private String id;

    @Column(unique = true)
    private UUID technicalId;

    @Column(name = "YEAR_VALUE")
    @NonNull
    private Integer year;

    @Column(name = "PERIOD_VALUE")
    @NonNull
    @Enumerated(EnumType.STRING)
    private PeriodEnum period;

    private String campaignWording;

    @Column(columnDefinition = "boolean default false")
    private boolean sensitivity = false;


    @Column(name = "datacollection_target")
    @Enumerated(EnumType.STRING)
    private DataCollectionEnum dataCollectionTarget = DataCollectionEnum.LUNATIC_NORMAL;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "campaign")
    private Set<Partitioning> partitionings;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @PrePersist
    public void generateUUID() {
        // Only generate a UUID if it's not already set (i.e., it's null)
        if (technicalId == null) {
            technicalId = UUID.randomUUID();
        }
    }

}
