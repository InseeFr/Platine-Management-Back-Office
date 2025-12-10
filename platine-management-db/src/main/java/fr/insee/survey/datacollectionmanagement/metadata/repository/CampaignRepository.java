package fr.insee.survey.datacollectionmanagement.metadata.repository;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SourceTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


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
            LEFT JOIN LATERAL (
                SELECT 1
                FROM partitioning p
                WHERE p.campaign_id = c.id
                  AND NOW() BETWEEN p.opening_date AND p.closing_date
                LIMIT 1
            ) p_open ON true
            WHERE (:source IS NULL OR UPPER(s2.id::text) = UPPER(:source))
            ORDER BY
                CASE WHEN p_open IS NOT NULL THEN 1 ELSE 0 END DESC,
                c.id ASC
        """, nativeQuery = true)
    Page<Campaign> findBySource(@Param("source") String source, Pageable pageable);

    List<Campaign> findByDataCollectionTargetIsNot(DataCollectionEnum dataCollectionTarget);

    @Query(value = """
        SELECT DISTINCT ON (c.id)
            c.*
        FROM campaign c
        JOIN partitioning p
            ON p.campaign_id = c.id
        JOIN survey s
            ON s.id = c.survey_id
        JOIN source src
            ON src.id = s.source_id
        WHERE p.opening_date <= :instant
          AND p.closing_date >= :instant
        """,
            nativeQuery = true)
    List<Campaign> findOpenedCampaigns(@Param("instant") Instant instant);

    @Query(value = """
        SELECT DISTINCT ON (c.id)
            c.*
        FROM campaign c
        JOIN partitioning p
            ON p.campaign_id = c.id
        JOIN survey s
            ON s.id = c.survey_id
        JOIN source src
            ON src.id = s.source_id
        JOIN user_wallet uw
            ON uw.source_id = src.id
        WHERE p.opening_date <= :instant
          AND p.closing_date >= :instant
          AND UPPER(uw.user_id) = UPPER(:userId)
        """,
            nativeQuery = true)
    List<Campaign> findOpenedCampaignsForUser(@Param("userId") String userId,
                                              @Param("instant") Instant instant);

    @Query(value = """
        SELECT DISTINCT ON (c.id)
            c.*
        FROM campaign c
        JOIN partitioning p
            ON p.campaign_id = c.id
        JOIN survey s
            ON s.id = c.survey_id
        JOIN source src
            ON src.id = s.source_id
        JOIN groups g
            ON g.source_id = src.id
        JOIN user_group ug
            ON ug.group_id = g.group_id
        WHERE p.opening_date <= :instant
          AND p.closing_date >= :instant
          AND UPPER(ug.user_id) = UPPER(:userId)
        """,
            nativeQuery = true)
    List<Campaign> findOpenedCampaignsForUserGroups(@Param("userId") String userId,
                                                    @Param("instant") Instant instant);

    @Query(value = """
    SELECT src.type
    FROM campaign c
        JOIN survey s ON s.id = c.survey_id
        JOIN source src ON src.id = s.source_id
    WHERE c.id = :idCampaign
    """, nativeQuery = true)
    Optional<SourceTypeEnum> findSourceTypeById(String idCampaign);
}
