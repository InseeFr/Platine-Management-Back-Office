package fr.insee.survey.datacollectionmanagement.user.domain;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_wallet",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_wallet_su_source",
                        columnNames = {"survey_unit_id", "source_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWallet {

    @EmbeddedId
    private UserWalletId id;

    @MapsId("surveyUnitId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_unit_id", referencedColumnName = "idSu",
            insertable = false, updatable = false)
    private SurveyUnit surveyUnit;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "identifier",
            insertable = false, updatable = false)
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

