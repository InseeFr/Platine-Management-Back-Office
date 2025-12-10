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
    @Column(name = "group_id")
    private UUID groupId;

    @Column(name = "survey_unit_id")
    private String surveyUnitId;
}

