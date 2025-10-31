package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.Groupe;
import fr.insee.survey.datacollectionmanagement.user.domain.GroupeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupeRepository extends JpaRepository<Groupe, GroupeId> {
    Optional<Groupe> findByIdSourceIdAndLabel(String sourceId, String label);
}

