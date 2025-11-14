package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.GroupEntity;
import fr.insee.survey.datacollectionmanagement.user.domain.UserGroup;
import fr.insee.survey.datacollectionmanagement.user.domain.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {

    List<UserGroup> findByIdGroupId(UUID groupId);
    List<UserGroup> findByIdUserId(String userId);
    void deleteAllByGroupIn(Collection<GroupEntity> groups);
}

