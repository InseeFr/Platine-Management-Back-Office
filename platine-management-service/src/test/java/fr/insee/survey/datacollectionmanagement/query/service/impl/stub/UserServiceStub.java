package fr.insee.survey.datacollectionmanagement.query.service.impl.stub;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class UserServiceStub implements UserService {

    @Setter
    private List<User> users = new ArrayList<>();

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Set<String> findMissingIdentifiers(Set<String> uniqueUsers) {
        if (uniqueUsers == null || uniqueUsers.isEmpty()) {
            return Set.of();
        }
        Set<String> existingUsers = users.stream().map(User::getIdentifier).collect(Collectors.toSet());
        Set<String> missingIdentifiers = new HashSet<>(uniqueUsers);
        missingIdentifiers.removeAll(existingUsers);
        return missingIdentifiers;
    }

    @Override
    public User findByIdentifier(String identifier) {
        return null;
    }

    @Override
    public Optional<User> findOptionalByIdentifier(String identifier) {
        return users.stream()
                .filter(user -> user.getIdentifier().equals(identifier))
                .findFirst();
    }

    @Override
    public User saveUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(String identifier) {
        // Stub
    }

    @Override
    public User createUser(User user, JsonNode payload) {
        return null;
    }

    @Override
    public User updateUser(User user, JsonNode payload) throws NotFoundException {
        return null;
    }

    @Override
    public void deleteUserAndEvents(User user) {
        // Stub
    }
}
