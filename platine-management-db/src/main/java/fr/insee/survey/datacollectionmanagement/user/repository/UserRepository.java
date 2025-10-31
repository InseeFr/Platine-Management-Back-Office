package fr.insee.survey.datacollectionmanagement.user.repository;

import fr.insee.survey.datacollectionmanagement.user.domain.User;
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

  /**
   * Effectue une recherche en masse pour trouver les identifiants d'utilisateurs qui EXISTENT
   * dans la base de données parmi l'ensemble fourni.
   * @param identifiers L'ensemble des identifiants à vérifier.
   * @return Une liste des identifiants qui existent en base de données.
   */
  @Query("SELECT u.identifier FROM User u WHERE u.identifier IN :identifiers")
  Set<String> findExistingIdentifiers(Set<String> identifiers);
}
