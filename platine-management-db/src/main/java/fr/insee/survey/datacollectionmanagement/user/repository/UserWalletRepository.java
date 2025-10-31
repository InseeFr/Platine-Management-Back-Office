package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.UserWallet;
import fr.insee.survey.datacollectionmanagement.user.domain.UserWalletId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWalletRepository extends JpaRepository<UserWallet, UserWalletId> {

    Optional<UserWallet> findByIdSurveyUnitIdAndIdGroupIdSourceId(String surveyUnitId, String sourceId);
}

