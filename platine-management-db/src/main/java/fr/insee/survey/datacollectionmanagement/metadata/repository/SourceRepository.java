package fr.insee.survey.datacollectionmanagement.metadata.repository;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Support;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SourceRepository extends JpaRepository<Source, String> {

    @Query("select s.support from Source s where s.id = :sourceId")
    Optional<Support> findSupportBySourceId(String sourceId);

}
