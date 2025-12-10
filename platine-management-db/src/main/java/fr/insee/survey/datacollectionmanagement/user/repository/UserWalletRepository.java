package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.UserWallet;
import fr.insee.survey.datacollectionmanagement.user.domain.UserWalletId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWalletRepository extends JpaRepository<UserWallet, UserWalletId> {

    void deleteAllByIdSourceId(String sourceId);

    List<UserWallet> findByIdSourceIdAndIdSurveyUnitId(String sourceId, String surveyUnitId);
}

