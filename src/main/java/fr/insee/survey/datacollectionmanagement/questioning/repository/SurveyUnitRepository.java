package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SurveyUnitRepository extends JpaRepository<SurveyUnit, String> {

    List<SurveyUnit> findAllByIdentificationCode(String identificationCode);

    List<SurveyUnit> findByIdentificationNameIgnoreCase(String identificationName);

    @Query(nativeQuery = true, value = """
                SELECT *  
                FROM survey_unit su
                WHERE (:id_su is null or upper(su.id_su) like upper(concat('%', :id_su, '%')))
                AND (:identification_name is null or upper(su.identification_name) like upper(concat('%', :identification_name, '%')))
                AND (:identification_code is null or upper(su.identification_code) like upper(concat('%', :identification_code, '%')))
            """)
    Page<SurveyUnit> findByParameters(@Param("id_su") String idSu, @Param("identification_code") String identificationCode, @Param("identification_name") String identificationName, Pageable pageable);


    @Query(nativeQuery = true, value = "SELECT *  FROM survey_unit ORDER BY random() LIMIT 1")
    SurveyUnit findRandomSurveyUnit();
}
