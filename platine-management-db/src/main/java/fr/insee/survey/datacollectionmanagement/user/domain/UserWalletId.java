package fr.insee.survey.datacollectionmanagement.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserWalletId implements Serializable {
    @Column(name = "user_id")
    private String userId;

    @Column(name = "survey_unit_id")
    private String surveyUnitId;

    @Column(name = "source_id")
    private String sourceId;
}

