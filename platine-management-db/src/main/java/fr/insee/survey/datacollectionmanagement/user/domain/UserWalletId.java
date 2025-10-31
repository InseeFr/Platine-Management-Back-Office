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
    private String surveyUnitId;
    private GroupeId groupId;
    private String userId;
}

