package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.UserGroup;
import fr.insee.survey.datacollectionmanagement.user.domain.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {

    Optional<UserGroup> findByIdUserIdAndIdGroupIdSourceId(String userId, String sourceId);
}

