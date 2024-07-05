package fr.insee.survey.datacollectionmanagement.query.service;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface CheckHabilitationService {

    boolean checkHabilitation(String role, String idSu, String campaign, List<String> userRoles, String userId);

}
