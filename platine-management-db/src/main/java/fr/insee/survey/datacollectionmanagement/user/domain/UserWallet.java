package fr.insee.survey.datacollectionmanagement.user.domain;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_wallet")
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

