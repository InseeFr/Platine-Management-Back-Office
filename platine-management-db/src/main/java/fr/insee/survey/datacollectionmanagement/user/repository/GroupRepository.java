package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
    Optional<GroupEntity> findBySource_IdAndLabel(String sourceId, String label);
    boolean existsBySource_IdAndLabel(String sourceId, String label);
    void deleteAllBySourceId(String sourceId);
    List<GroupEntity> findAllBySourceId(String sourceId);
}

