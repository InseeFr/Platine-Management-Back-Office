package fr.insee.survey.datacollectionmanagement.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GroupWalletId implements Serializable {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "sourceId", column = @Column(name = "source_id")),
            @AttributeOverride(name = "groupId",  column = @Column(name = "group_id"))
    })
    private GroupeId groupId;
    private String surveyUnitId;
}

