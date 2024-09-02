package fr.insee.survey.datacollectionmanagement.contact.repository;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.dto.SearchContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactRepository extends PagingAndSortingRepository<Contact, String>, JpaRepository<Contact, String> {

    @Override
    Page<Contact> findAll(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT *  FROM contact ORDER BY random() LIMIT 1")
    Contact findRandomContact();

    @Query(nativeQuery = true, value = "SELECT identifier FROM contact TABLESAMPLE system_rows(1)")
    String findRandomIdentifierContact();
    @Query(
            value = """
        SELECT
            c.identifier as identifier,
            c.email as email,
            c.first_name as firstName,
            c.last_name as lastName
        FROM
            contact c
        WHERE
            UPPER(c.identifier) LIKE CONCAT(:param, '%')           
        """,
            nativeQuery = true
    )
    Page<SearchContactDto> findByIdentifier( String param, Pageable pageable);

    @Query(
            value = """
        SELECT
            c.identifier as identifier,
            c.email as email,
            c.first_name as firstName,
            c.last_name as lastName
        FROM
            contact c
        WHERE
            UPPER(c.email) LIKE CONCAT(:param, '%')   
        """,
            nativeQuery = true
    )
    Page<SearchContactDto> findByEmail( String param, Pageable pageable);
    @Query(
            value = """
        SELECT
            c.identifier as identifier,
            c.email as email,
            c.first_name as firstName,
            c.last_name as lastName
        FROM
            contact c
        WHERE
            UPPER(c.last_name) LIKE CONCAT(:param, '%')
            OR UPPER(CONCAT(c.first_name, ' ', c.last_name)) LIKE CONCAT(:param, '%')            
        """,
            nativeQuery = true
    )
    Page<SearchContactDto> findByFirstNameLastName(String param, Pageable pageable);

}
