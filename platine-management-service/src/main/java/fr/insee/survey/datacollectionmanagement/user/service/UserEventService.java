package fr.insee.survey.datacollectionmanagement.user.service;


import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.domain.UserEvent;
import fr.insee.survey.datacollectionmanagement.user.enums.UserEventTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface UserEventService {

    Page<UserEvent> findAll(Pageable pageable);

    UserEvent findById(Long id);

    Set<UserEvent> findUserEventsByUser (User user);

    UserEvent createUserEvent(User user, UserEventTypeEnum type, JsonNode payload);

}

