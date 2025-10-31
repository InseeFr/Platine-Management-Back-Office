package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.GroupWallet;
import fr.insee.survey.datacollectionmanagement.user.domain.GroupWalletId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GroupWalletRepository extends JpaRepository<GroupWallet, GroupWalletId> {

    Optional<GroupWallet> findByIdGroupIdSourceIdAndIdSurveyUnitId(String sourceId, String surveyUnitId);
}

