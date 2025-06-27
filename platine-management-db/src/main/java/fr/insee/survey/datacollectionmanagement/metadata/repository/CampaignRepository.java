package fr.insee.survey.datacollectionmanagement.metadata.repository;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface CampaignRepository extends JpaRepository<Campaign, String>,PagingAndSortingRepository<Campaign, String> {

    static final String QUERY_FIND_CAMPAIGN =
        "select                                                                                                         "
            + "        *                                                                                                "
            + "from                                                                                                     "
            + "        campaign c                                                                                       "
            + "join survey s                                                                                            "
            + "on                                                                                                       "
            + "        s.id = c.survey_id                                                                               "
            + "join \"source\" s2                                                                                       "
            + "on                                                                                                       "
            + "        s2.id = s.source_id                                                          "
            + "where                                                                                                    "
            + "        (:source is null or UPPER(s2.id) = UPPER(cast( :source as text)))                         "
            + "        and (:period is null or UPPER(c.period_value) = UPPER(cast( :period as text)))                     ";
    
    static final String CLAUSE_YEAR = " and (:year is null or s.year_value = :year)                                       ";

    List<Campaign> findByPeriod(String period);

    @Query(nativeQuery = true, value = QUERY_FIND_CAMPAIGN + CLAUSE_YEAR)
    List<Campaign> findBySourceYearPeriod(String source, Integer year, String period);
    
    @Query(nativeQuery = true, value = QUERY_FIND_CAMPAIGN)
    List<Campaign> findBySourcePeriod(String source, String period);

    @Query(value = """
        SELECT c.* 
        FROM campaign c
        JOIN survey s ON s.id = c.survey_id
        JOIN "source" s2 ON s2.id = s.source_id
        WHERE (:source IS NULL OR UPPER(s2.id) = UPPER(CAST(:source AS TEXT)))
        """, nativeQuery = true)
    Page<Campaign> findBySource(String source, Pageable pageable);

    List<Campaign> findByDataCollectionTargetIsNot(DataCollectionEnum dataCollectionTarget);
}
