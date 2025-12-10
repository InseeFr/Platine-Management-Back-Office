package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.GroupEntity;
import fr.insee.survey.datacollectionmanagement.user.domain.GroupWallet;
import fr.insee.survey.datacollectionmanagement.user.domain.GroupWalletId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GroupWalletRepository extends JpaRepository<GroupWallet, GroupWalletId> {

    List<GroupWallet> findByIdSurveyUnitId(String surveyUnitId);

    void deleteAllByGroupIn(Collection<GroupEntity> groups);
}

