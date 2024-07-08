package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestioningAccreditationRepository extends JpaRepository<QuestioningAccreditation, Long> {

    String QUERY_FIND_IDCONTACT =
        "select id_contact  from questioning_accreditation qa                                                                                           "
            + "     join questioning q                                                                                                                  "
            + "     on q.id =qa.questioning_id                                                                                                          "
            + "     where q.id_partitioning = ?1                                                                                                        ";

    String QUERY_FIND_IDPARTIONING =
        "select q.id_partitioning  from questioning q                                                                                                   "
            + "join questioning_accreditation qa                                                                                                        "
            + "on q.id =qa.questioning_id                                                                                                               "
            + "where qa.id_contact =?1                                                                                                                  ";

    String FIND_METADATA_COPY =
        "select                                                                                                                                         "
            + "        id_contact                                                                                                                       "
            + "from                                                                                                                                     "
            + "        questioning_accreditation qa                                                                                                     "
            + "join questioning q  on  q.id = qa.questioning_id                                                                                         "
            +" join metadata_copy mc on mc.id_partitioning = q.id_partitioning                                                                          ";


    String QUERY_SOURCE_YEAR_PERIOD = FIND_METADATA_COPY + " where mc.id = ?1 and mc.year_value = ?2 and mc.period_value = ?3 ";

    String QUERY_SOURCE = FIND_METADATA_COPY + " where mc.id = ?1 ";

    String QUERY_YEAR = FIND_METADATA_COPY + " where mc.year_value = ?1  ";

    String QUERY_PERIOD = FIND_METADATA_COPY + " where mc.period_value = ?1 ";

    List<QuestioningAccreditation> findByIdContact(String idContact);

    @Query(nativeQuery = true, value = QUERY_FIND_IDCONTACT)
    List<String> findIdContactsByPartitionigAccredications(String idPartitioning);

    @Query(nativeQuery = true, value = QUERY_FIND_IDPARTIONING)
    List<String> findIdPartitioningsByContactAccreditations(String idContact);

    @Query(nativeQuery = true, value = QUERY_SOURCE)
    List<String> findIdContactsByIdSource(String idSource);

    @Query(nativeQuery = true, value = QUERY_YEAR)
    List<String> findIdContactsByYear(Integer year);

    @Query(nativeQuery = true, value = QUERY_PERIOD)
    List<String> findIdContactsByPeriod(String period);

    @Query(nativeQuery = true, value = QUERY_SOURCE_YEAR_PERIOD)
    List<String> findIdContactsBySourceYearPeriod(String source, Integer year, String period);

}
