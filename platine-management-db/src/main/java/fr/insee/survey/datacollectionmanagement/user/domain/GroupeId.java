package fr.insee.survey.datacollectionmanagement.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GroupeId implements Serializable {

    @Column(name = "source_id")
    private String sourceId;
    @Column(name = "group_id")
    private UUID groupId;
}

