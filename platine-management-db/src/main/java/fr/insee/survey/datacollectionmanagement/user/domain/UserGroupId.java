package fr.insee.survey.datacollectionmanagement.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserGroupId implements Serializable {

    private String userId;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "sourceId", column = @Column(name = "source_id")),
            @AttributeOverride(name = "groupId",  column = @Column(name = "group_id"))
    })
    private GroupeId groupId;
}

