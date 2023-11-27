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
    public Page<User> findAll(Pageable pageable);

    /**
     * Find a user by its identifier.
     *
     * @param identifier
     * @return Optional user found
     */
    public Optional<User> findByIdentifier(String identifier) ;

    /**
     * Update an existing user , or creates a new one
     *
     * @param user
     * @return user updated
     */
    public User saveUser(User user);

    /**
     * Delete a user.
     * @param identifier
     */
    public void deleteUser(String identifier);

    public User createUser(User user, JsonNode payload);

    public User updateUser(User user, JsonNode payload) throws NotFoundException;

    public void deleteUserAndEvents(User user);

    List<String> findAccreditedSources(String identifier);
}