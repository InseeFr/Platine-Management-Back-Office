package fr.insee.survey.datacollectionmanagement.user.domain;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groupe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Groupe {

    @EmbeddedId
    private GroupeId id;

    @Column(name = "label", nullable = false)
    private String label;

    @MapsId("sourceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Source source;
}
