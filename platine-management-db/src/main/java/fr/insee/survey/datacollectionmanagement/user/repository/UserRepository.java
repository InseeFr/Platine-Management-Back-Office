package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends PagingAndSortingRepository<User, String>, JpaRepository<User, String> {


    Optional<User> findByIdentifierIgnoreCase(String identifier);

    @Query("select distinct upper(u.identifier) from User u where upper(u.identifier) in :identifiers")
    Set<String> findExistingUserIdentifiers(Collection<String> identifiers);

    List<User> findAllByIdentifierInIgnoreCase(Collection<String> identifiers);
}
