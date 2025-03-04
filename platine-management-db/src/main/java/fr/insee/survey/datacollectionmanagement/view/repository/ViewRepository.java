package fr.insee.survey.datacollectionmanagement.view.repository;

import fr.insee.survey.datacollectionmanagement.view.domain.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewRepository extends PagingAndSortingRepository<View, Long>, JpaRepository<View, Long> {

    String FIND_DISTNCT_VIEW_BY_IDENTIFIER = """
            select
                  distinct on
                  (v.identifier)
                  v.id as id,
                  v.identifier as identifier,
                  v.campaign_id as campaign_id,
                  v.id_su as id_su
            from
                  view v
            where
                 campaign_id =?1""";

    String FIND_DISTNCT_CAMPAIGN_BY_IDENTIFIER = """
            select
                 distinct v.campaign_id
            from
                 view v
            where
                 v.identifier = ?1
            and
                 v.campaign_id is not null""";

    View findFirstByIdentifier(String identifier);

    List<View> findByIdentifier(String identifier);

    @Query(nativeQuery = true, value = FIND_DISTNCT_VIEW_BY_IDENTIFIER)
    List<View> findDistinctViewByCampaignId(String campaignId);

    @Query(nativeQuery = true, value = FIND_DISTNCT_CAMPAIGN_BY_IDENTIFIER)
    List<String> findDistinctCampaignByIdentifier(String campaignId);

    List<View> findByIdSu(String idSu);

    List<View> findByIdSuContaining(String field);

    Long countViewByIdentifierAndIdSuAndCampaignId(String identifier, String idSu, String campaignId);

    List<View> findByIdentifierContainingAndIdSuNotNull(String identifier);

    void deleteByIdentifier(String identifier);

    List<View> findByIdentifierIn(@Param("identifiers") List<String> identifiers);


}
