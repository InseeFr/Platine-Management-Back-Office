package fr.insee.survey.datacollectionmanagement.user.domain;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_wallet",
        indexes = {
                @Index(name = "idx_user_wallet_su", columnList = "survey_unit_id"),
                @Index(name = "idx_user_wallet_source", columnList = "source_id"),
                @Index(name = "idx_user_wallet_user_source", columnList = "user_id,source_id"),
                @Index(name = "idx_user_wallet_source_su", columnList = "source_id,survey_unit_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserWallet {

    @EmbeddedId
    private UserWalletId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "identifier", nullable = false, insertable = false, updatable = false)
    private User user;

    @MapsId("surveyUnitId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_unit_id", referencedColumnName = "idSu", nullable = false, insertable = false, updatable = false)
    private SurveyUnit surveyUnit;

    @MapsId("sourceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Source source;
}

