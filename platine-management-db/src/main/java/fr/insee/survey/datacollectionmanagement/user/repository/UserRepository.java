package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, String>, JpaRepository<User, String> {

    Page<User> findAll(Pageable pageable);

    Optional<User> findByIdentifierIgnoreCase(String identifier);

    @Query("select distinct u.identifier from User u where upper(u.identifier) in :identifiers")
    Set<String> findExistingUserIdentifiers(Collection<String> identifiers);

    List<User> findAllByIdentifierInIgnoreCase(Collection<String> identifiers);
}
