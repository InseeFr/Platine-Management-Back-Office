package fr.insee.survey.datacollectionmanagement.user.domain;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_group",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_group_user_source",
                        columnNames = {"user_id", "source_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {

    @EmbeddedId
    private UserGroupId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "identifier")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    private Source source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", referencedColumnName = "source_id",
                    insertable = false, updatable = false)
    @JoinColumn(name = "group_id", referencedColumnName = "group_id",
                    insertable = false, updatable = false)
    private Groupe groupe;
}

