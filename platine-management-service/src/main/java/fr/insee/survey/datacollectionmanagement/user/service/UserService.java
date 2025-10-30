package fr.insee.survey.datacollectionmanagement.user.service;


import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    /**
     * Find all users
     *
     * @param pageable
     * @return user Page
     */
    Page<User> findAll(Pageable pageable);

    List<User> findAll();

    /**
     * Find a user by its identifier.
     *
     * @param identifier
     * @return Optional user found
     */
    User findByIdentifier(String identifier) ;

    /**
     * Find a user by its identifier.
     *
     * @param identifier
     * @return Optional user found
     */
    Optional<User> findOptionalByIdentifier(String identifier) ;

    /**
     * Update an existing user , or creates a new one
     *
     * @param user
     * @return user updated
     */
    User saveUser(User user);

    /**
     * Delete a user.
     * @param identifier
     */
    void deleteUser(String identifier);

    User createUser(User user, JsonNode payload);

    User updateUser(User user, JsonNode payload) throws NotFoundException;

    void deleteUserAndEvents(User user);
}