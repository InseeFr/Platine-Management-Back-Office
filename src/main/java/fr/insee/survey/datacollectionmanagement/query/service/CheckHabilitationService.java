package fr.insee.survey.datacollectionmanagement.query.service;

import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthUser;
import org.springframework.stereotype.Service;


@Service
public interface CheckHabilitationService {

    boolean checkHabilitation(String role, String idSu, String campaign, AuthUser authUser);

}
