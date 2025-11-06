package fr.insee.survey.datacollectionmanagement.user.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "groups",
        uniqueConstraints = {
                @UniqueConstraint(name = "unik_source_group_label", columnNames = {"source_id","label"})
        },
        indexes = {
                @Index(name = "idx_groupe_source", columnList = "source_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "groupId")
public class GroupEntity {

    @Id
    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "label", nullable = false)
    private String label;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false, referencedColumnName = "id")
    private Source source;

    @PrePersist
    void ensureId() {
        if (groupId == null) {
            groupId = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
