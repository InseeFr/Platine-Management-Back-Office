package fr.insee.survey.datacollectionmanagement.user.domain;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupWallet {

    @EmbeddedId
    private GroupWalletId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false)
    private GroupEntity group;

    @MapsId("surveyUnitId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_unit_id", referencedColumnName = "idSu", nullable = false, insertable = false, updatable = false)
    private SurveyUnit surveyUnit;
}

