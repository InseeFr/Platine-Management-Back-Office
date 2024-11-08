package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SurveyUnitRepository extends JpaRepository<SurveyUnit, String> {

    List<SurveyUnit> findAllByIdentificationCode(String identificationCode);

    @Query(nativeQuery = true, value = """
                SELECT 
                    *  
                FROM 
                    survey_unit su
                WHERE
                    UPPER(su.id_su) LIKE CONCAT(UPPER(:param), '%')                      
            """)
    Page<SearchSurveyUnitDto> findByIdentifier(String param, Pageable pageable);

    @Query(nativeQuery = true, value = """
                SELECT 
                    *  
                FROM 
                    survey_unit su
                WHERE
                    UPPER(su.identification_code) LIKE CONCAT(UPPER(:param), '%')
                       
            """)
    Page<SearchSurveyUnitDto> findByIdentificationCode(String param, Pageable pageable);

    @Query(nativeQuery = true, value = """
                SELECT 
                    *  
                FROM 
                    survey_unit su
                WHERE
                    UPPER(su.identification_name) LIKE CONCAT(UPPER(:param), '%')
                    
            """)
    Page<SearchSurveyUnitDto> findByIdentificationName(String param, Pageable pageable);
}
