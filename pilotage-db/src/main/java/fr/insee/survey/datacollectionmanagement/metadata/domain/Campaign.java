package fr.insee.survey.datacollectionmanagement.metadata.domain;

import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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
    
    @Column(name = "YEAR_VALUE")
    @NonNull 
    private Integer year;
    
    @Column(name = "PERIOD_VALUE")
    @NonNull
    @Enumerated(EnumType.STRING)
    private PeriodEnum period;
    
    private String campaignWording;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Partitioning> partitionings;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;

    @ManyToOne
    private Survey survey;

}
