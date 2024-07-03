package fr.insee.survey.datacollectionmanagement.query.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public interface CheckHabilitationService {

    boolean checkHabilitation(String role, String idSu, String campaign, Authentication authUser);

}
