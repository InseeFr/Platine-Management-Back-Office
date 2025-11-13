package fr.insee.survey.datacollectionmanagement.integration;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import fr.insee.survey.datacollectionmanagement.user.dao.WalletDao;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.enums.UserRoleTypeEnum;
import fr.insee.survey.datacollectionmanagement.user.service.impl.UserServiceImpl;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class InternalUserSteps {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    WalletDao walletDao;

    @Autowired
    SourceService sourceService;

    @Given("the user {string}")
    public void createUser(String userId) {
        User user = new User();
        user.setIdentifier(userId);
        user.setRole(UserRoleTypeEnum.GESTIONNAIRE);
        userService.createUser(user, null);
    }

    @Given("the user_wallet for user {string} with survey_unit {string} with group {} and source {string}")
    public void createWalletForUser(String userId, String surveyUnitId, String groupId, String sourceId) {
        Source source = sourceService.findById(sourceId);
        walletDao.insertWallets(source, List.of(new WalletDto(surveyUnitId,userId,groupId)));
    }
}
