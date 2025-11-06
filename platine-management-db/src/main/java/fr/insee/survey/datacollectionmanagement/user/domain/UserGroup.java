package fr.insee.survey.datacollectionmanagement.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_group",
        indexes = {
                @Index(name = "idx_user_group_group_id", columnList = "group_id"),
                @Index(name = "idx_user_group_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {

    @EmbeddedId
    private UserGroupId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id", nullable = false, referencedColumnName = "group_id")
    private GroupEntity group;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "identifier", insertable = false, updatable = false)
    private User user;
}

