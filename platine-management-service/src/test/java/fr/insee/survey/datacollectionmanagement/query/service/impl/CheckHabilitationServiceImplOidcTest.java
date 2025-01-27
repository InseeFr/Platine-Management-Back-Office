package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.UserServiceStub;
import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.ViewServiceStub;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.enums.UserRoleTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CheckHabilitationServiceImplOidcTest {
    private CheckHabilitationServiceImplOidc checkHabilitationServiceImplOidc;
    private ViewServiceStub viewServiceStub;
    private UserServiceStub userServiceStub;

    @BeforeEach
    void init() {
        viewServiceStub = new ViewServiceStub();
        userServiceStub = new UserServiceStub();
        checkHabilitationServiceImplOidc = new CheckHabilitationServiceImplOidc(viewServiceStub, userServiceStub);
    }

    @Test
    @DisplayName("Should return true if user is admin")
    void should_return_true_if_user_is_admin() {
        //given
        List<String> userRoles = List.of(AuthorityRoleEnum.ADMIN.securityRole());

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(null,
                "id-su",
                "campaign-id",
                userRoles,
                "user-id");

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false if habilitation role is not reviewer")
    void should_return_false_if_habilitation_role_is_not_reviewer() {
        //given
        List<String> userRoles = List.of();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                AuthorityRoleEnum.INTERNAL_USER.securityRole(),
                "id-su",
                "campaign-id",
                userRoles,
                "user-id");

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false if habilitation role is reviewer and user not found")
    void should_return_false_if_habilitation_role_is_reviewer_and_user_not_found() {
        //given
        List<String> userRoles = List.of();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.REVIEWER,
                "id-su",
                "campaign-id",
                userRoles,
                "user-id");

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false if habilitation role is reviewer and user has internal user role and assistance role")
    void should_return_false_if_habilitation_role_is_reviewer_and_user_has_internal_user_role_and_assistance_role() {
        //given
        List<String> userRoles = List.of(AuthorityRoleEnum.INTERNAL_USER.securityRole());
        String userId = "user-id";
        User user = new User();
        user.setIdentifier(userId);
        user.setRole(UserRoleTypeEnum.ASSISTANCE);
        userServiceStub.setUsers(List.of(user));

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.REVIEWER,
                "id-su",
                "campaign-id",
                userRoles,
                userId);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false if habilitation role is reviewer and user has internal user role and no assistance role")
    void should_return_true_if_habilitation_role_is_reviewer_and_user_has_internal_user_role_and_no_assistance_role() {
        //given
        List<String> userRoles = List.of(AuthorityRoleEnum.INTERNAL_USER.securityRole());
        String userId = "user-id";
        User user = new User();
        user.setIdentifier(userId);
        user.setRole(UserRoleTypeEnum.GESTIONNAIRE);
        userServiceStub.setUsers(List.of(user));

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.REVIEWER,
                "id-su",
                "campaign-id",
                userRoles,
                userId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false if habilitation role is reviewer and user has no internal user role")
    void should_return_false_if_habilitation_role_is_reviewer_and_user_has_no_internal_user_role() {
        //given
        List<String> userRoles = List.of(AuthorityRoleEnum.RESPONDENT.securityRole());
        String userId = "user-id";
        User user = new User();
        user.setIdentifier(userId);
        user.setRole(UserRoleTypeEnum.GESTIONNAIRE);
        userServiceStub.setUsers(List.of(user));

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.REVIEWER,
                "id-su",
                "campaign-id",
                userRoles,
                userId);

        //then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", UserRoles.INTERVIEWER})
    @NullSource
    @DisplayName("Should return false if role undefined and user is not respondant")
    void should_return_false_if_role_undefined_and_user_is_not_respondant(String habilitationRole) {
        //given
        List<String> userRoles = List.of("plop", "test", AuthorityRoleEnum.INTERNAL_USER.securityRole());

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(habilitationRole,
                "id-su",
                "campaign-id",
                userRoles,
                "user-id");

        //then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", UserRoles.INTERVIEWER})
    @NullSource
    @DisplayName("Should return true if role undefined and user is respondant and count view different from 0")
    void should_return_true_if_role_undefined_and_user_is_respondant_and_count_view_different_from_zero(String habilitationRole) {
        //given
        List<String> userRoles = List.of("plop", "test", AuthorityRoleEnum.RESPONDENT.securityRole());
        viewServiceStub.setCountViewByIdentifier(1L);

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(habilitationRole,
                "id-su",
                "campaign-id",
                userRoles,
                "user-id");

        //then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", UserRoles.INTERVIEWER})
    @NullSource
    @DisplayName("Should return false if role undefined and user is respondant and count view equals to 0")
    void should_return_false_if_role_undefined_and_user_is_respondant_and_count_view_equals_to_zero(String habilitationRole) {
        //given
        List<String> userRoles = List.of("plop", "test", AuthorityRoleEnum.RESPONDENT.securityRole());
        viewServiceStub.setCountViewByIdentifier(0L);

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(habilitationRole,
                "id-su",
                "campaign-id",
                userRoles,
                "user-id");

        //then
        assertThat(result).isFalse();
    }
}
