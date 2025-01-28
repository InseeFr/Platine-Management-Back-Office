package fr.insee.survey.datacollectionmanagement.user.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.user.domain.SourceAccreditation;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.domain.UserEvent;
import fr.insee.survey.datacollectionmanagement.user.enums.UserEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.user.repository.UserRepository;
import fr.insee.survey.datacollectionmanagement.user.service.SourceAccreditationService;
import fr.insee.survey.datacollectionmanagement.user.service.UserEventService;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserEventService userEventService;

    private final UserRepository userRepository;

    private final SourceAccreditationService sourceAccreditationService;

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByIdentifier(String identifier) {
        return findOptionalByIdentifier(identifier)
                .orElseThrow(()->
                        new NotFoundException(
                                String.format("User %s not found", identifier)
                        )
                );
    }

    @Override
    public Optional<User> findOptionalByIdentifier(String identifier) {
        return userRepository.findByIdentifierIgnoreCase(identifier);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String identifier) {
        userRepository.deleteById(identifier);
    }


    @Override
    public User createUser(User user, JsonNode payload) {

        UserEvent newUserEvent = userEventService.createUserEvent(user, UserEventTypeEnum.CREATE,
                payload);
        user.setUserEvents(new HashSet<>(Arrays.asList(newUserEvent)));
        return saveUser(user);
    }

    @Override
    public User updateUser(User user, JsonNode payload) {

        User existingUser = findByIdentifier(user.getIdentifier());

        Set<UserEvent> setUserEventsUser = existingUser.getUserEvents();
        UserEvent userEventUpdate = userEventService.createUserEvent(user, UserEventTypeEnum.UPDATE,
                payload);
        setUserEventsUser.add(userEventUpdate);
        user.setUserEvents(setUserEventsUser);
        return saveUser(user);
    }

    @Override
    public void deleteUserAndEvents(User user) {
        deleteUser(user.getIdentifier());
    }

    @Override
    public List<String> findAccreditedSources(String identifier){

        List<String> accreditedSources = new ArrayList<>();
        List<SourceAccreditation> accreditations = sourceAccreditationService.findByUserIdentifier(identifier);
        List<Source> accSource = accreditations.stream().map(SourceAccreditation::getSource).toList();
        accSource.forEach(acc -> accreditedSources.add(acc.getId()));

        return accreditedSources;
    }
}
