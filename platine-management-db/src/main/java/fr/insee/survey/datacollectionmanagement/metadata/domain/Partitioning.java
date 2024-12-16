package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "campainId_index", columnList = "campaign_id")
})
public class Partitioning {

    @Id
    private String id;
    @NonNull
    private String shortWording;
    private String label;
    private Date openingDate;
    private Date closingDate;
    private Date returnDate;

    @ManyToOne
    private Campaign campaign;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;

}
