package fr.insee.survey.datacollectionmanagement.metadata.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;

public interface SurveyRepository extends JpaRepository<Survey, String>, PagingAndSortingRepository<Survey, String>  {

    static final String QUERY_FIND_SURVEY =
            """
            select                                                                                                         
            su.*                                                                                                
            from                                                                                                     
            survey su                                                                                       
            join source so                                                                                            
            on (so.id=su.source_id)                                                   
            where                                                                                                    
            (:sourceId is null or UPPER(su.source_id) = UPPER(cast( :sourceId as text)))                         
            and (:periodicity is null or UPPER(so.periodicity) = UPPER(cast( :periodicity as text)))     
            and (:year is null or su.year_value = :year) 
            """;
    @Query(nativeQuery = true, value = QUERY_FIND_SURVEY)
    Page<Survey> findBySourceIdYearPeriodicity(Pageable pageable, String sourceId, Integer year, String periodicity);
}
