package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ContactAccreditedToSurveyUnitDto;
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
                    UPPER(su.id_su) LIKE :param || '%'                      
            """)
    Page<SearchSurveyUnitDto> findByIdentifier(String param, Pageable pageable);

    @Query(nativeQuery = true, value = """
                SELECT 
                    *  
                FROM 
                    survey_unit su
                WHERE
                    UPPER(su.identification_code) LIKE :param || '%'
            
            """)
    Page<SearchSurveyUnitDto> findByIdentificationCode(String param, Pageable pageable);

    @Query(nativeQuery = true, value = """
                SELECT 
                    *  
                FROM 
                    survey_unit su
                WHERE
                    UPPER(su.identification_name) LIKE :param || '%'
            
            """)
    Page<SearchSurveyUnitDto> findByIdentificationName(String param, Pageable pageable);

    @Query(nativeQuery = true,
            value = """
                    SELECT 
                            *  
                        FROM 
                            survey_unit su
                        WHERE
                            UPPER(su.id_su) LIKE :param || '%'  
                    UNION ALL
                    SELECT 
                            *  
                        FROM 
                            survey_unit su
                        WHERE
                            UPPER(su.identification_name) LIKE :param || '%'
                    UNION ALL
                    SELECT 
                            *  
                        FROM 
                            survey_unit su
                        WHERE
                            UPPER(su.identification_code) LIKE :param || '%'
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM (
                        SELECT 1 FROM survey_unit su
                        WHERE  UPPER(su.id_su) LIKE :param || '%'
                        UNION ALL
                        SELECT 1 FROM survey_unit su
                        WHERE UPPER(su.identification_name) LIKE :param || '%'
                        UNION ALL
                        SELECT 1 FROM survey_unit su
                        WHERE  UPPER(su.identification_code) LIKE :param || '%'
                    ) AS count_query""")
    Page<SearchSurveyUnitDto> findByParam(String param, Pageable pageable);

    @Query(nativeQuery = true, value = """
                SELECT
                    qa.id_contact as contact_id,
                    qa.is_main ,
                    string_agg(DISTINCT p.campaign_id::text, ', ') AS campaignIds
                FROM
                    questioning_accreditation qa
                JOIN
                    questioning q ON q.id = qa.questioning_id
                JOIN
                    partitioning p ON p.id = q.id_partitioning
                where q.survey_unit_id_su = :surveyUnitId
                GROUP BY
                    qa.id_contact,
                    qa.is_main
            """)
    List<ContactAccreditedToSurveyUnitDto> findContactsAccreditedToSurveyUnit(String surveyUnitId);

    @Query(value = """
        SELECT distinct p.campaign_id
        FROM partitioning p
        JOIN questioning q ON q.id_partitioning = p.id
        WHERE q.survey_unit_id_su = :surveyUnitId
        """, nativeQuery = true)
    List<String> findCampaignIdsBySurveyUnitId(String surveyUnitId);
}
