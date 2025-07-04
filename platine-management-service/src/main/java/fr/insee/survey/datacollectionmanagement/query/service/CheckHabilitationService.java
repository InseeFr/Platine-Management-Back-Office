package fr.insee.survey.datacollectionmanagement.query.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public interface CheckHabilitationService {

    boolean checkHabilitation(String role, String idSu, String campaign, List<String> userRoles, String userId);
    boolean checkHabilitation(String role, UUID questioningId, List<String> userRoles, String userId);

}
