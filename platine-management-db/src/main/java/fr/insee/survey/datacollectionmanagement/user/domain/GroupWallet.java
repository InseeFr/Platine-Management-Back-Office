package fr.insee.survey.datacollectionmanagement.user.domain;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
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
    @JoinColumn(name = "source_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    private Source source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", referencedColumnName = "source_id",
            insertable = false, updatable = false)
    @JoinColumn(name = "group_id", referencedColumnName = "group_id",
            insertable = false, updatable = false)
    private Groupe groupe;

    @MapsId("surveyUnitId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_unit_id", referencedColumnName = "idSu",
            insertable = false, updatable = false)
    private SurveyUnit surveyUnit;
}

