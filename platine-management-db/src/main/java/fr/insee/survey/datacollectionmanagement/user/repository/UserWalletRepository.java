package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.UserWallet;
import fr.insee.survey.datacollectionmanagement.user.domain.UserWalletId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserWalletRepository extends JpaRepository<UserWallet, UserWalletId> {

    List<UserWallet> findByIdUserIdAndIdSourceId(String userId, String sourceId);
    boolean existsByIdUserIdAndIdSurveyUnitIdAndIdSourceId(String userId, String suId, String sourceId);
    void deleteAllByIdSourceId(String sourceId);

    List<UserWallet> findByIdSourceIdAndIdSurveyUnitId(String sourceId, String surveyUnitId);
}

