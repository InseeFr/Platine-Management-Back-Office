package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnExpression("'${fr.insee.datacollectionmanagement.auth.mode}' ne 'OIDC'")
@Slf4j
public class CheckHabilitationServiceImplNoAuth implements CheckHabilitationService {

    @Override
    public boolean checkHabilitation(String role, String idSu, String campaignId, Authentication authUser) {
        return true;
    }

}
