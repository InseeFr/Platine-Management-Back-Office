package fr.insee.survey.datacollectionmanagement.query.service.impl.stub;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    }

    @Override
    public List<String> findAccreditedSources(String identifier) {
        return List.of();
    }
}
