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

    List<SurveyUnit> findByIdentificationNameIgnoreCase(String identificationName);

    @Query(nativeQuery = true, value = """
                SELECT 
                    *  
                FROM 
                    survey_unit su
                WHERE
                    :param IS NULL
                    OR
                        (
                         UPPER(su.id_su) LIKE CONCAT('%', :param, '%')
                         OR UPPER(su.identification_name) LIKE CONCAT('%', :param, '%')
                         OR UPPER(su.identification_code) LIKE CONCAT('%', :param, '%')
                        )
            """)
    Page<SearchSurveyUnitDto> findByParameters(String param, Pageable pageable);


    @Query(nativeQuery = true, value = "SELECT *  FROM survey_unit ORDER BY random() LIMIT 1")
    SurveyUnit findRandomSurveyUnit();
}
