package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.UserServiceStub;
import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.ViewServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningAccreditationServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningServiceStub;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.enums.UserRoleTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CheckHabilitationServiceImplOidcTest {
    private CheckHabilitationServiceImplOidc checkHabilitationServiceImplOidc;
    private ViewServiceStub viewServiceStub;
    private UserServiceStub userServiceStub;
    private QuestioningAccreditationServiceStub questioningAccreditationServiceStub;
    private QuestioningServiceStub questioningService;

    @BeforeEach
    void init() {
        viewServiceStub = new ViewServiceStub();
        userServiceStub = new UserServiceStub();
        questioningAccreditationServiceStub = new QuestioningAccreditationServiceStub();
        questioningService = new QuestioningServiceStub();
        checkHabilitationServiceImplOidc = new CheckHabilitationServiceImplOidc(
                viewServiceStub,
                userServiceStub,
                questioningAccreditationServiceStub,
                questioningService);
    }

    @Test
    @DisplayName("Should return true if user is admin")
    void should_return_true_if_user_is_admin_v1() {
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
    void should_return_false_if_habilitation_role_is_not_reviewer_v1() {
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
    void should_return_false_if_habilitation_role_is_reviewer_and_user_not_found_v1() {
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
    void should_return_false_if_habilitation_role_is_reviewer_and_user_has_internal_user_role_and_assistance_role_v1() {
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
    void should_return_true_if_habilitation_role_is_reviewer_and_user_has_internal_user_role_and_no_assistance_role_v1() {
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
    void should_return_false_if_habilitation_role_is_reviewer_and_user_has_no_internal_user_role_v1() {
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
    void should_return_false_if_role_undefined_and_user_is_not_respondant_v1(String habilitationRole) {
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
    void should_return_true_if_role_undefined_and_user_is_respondant_and_count_view_different_from_zero_v1(String habilitationRole) {
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
    void should_return_false_if_role_undefined_and_user_is_respondant_and_count_view_equals_to_zero_v1(String habilitationRole) {
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

    @Test
    @DisplayName("Should return true if user is admin")
    void should_return_true_if_user_is_admin() {
        //given
        List<String> userRoles = List.of(AuthorityRoleEnum.ADMIN.securityRole());
        UUID questioningId = UUID.randomUUID();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(null,
                questioningId,
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
        UUID questioningId = UUID.randomUUID();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                AuthorityRoleEnum.INTERNAL_USER.securityRole(),
                questioningId,
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
        UUID questioningId = UUID.randomUUID();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.REVIEWER,
                questioningId,
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
        UUID questioningId = UUID.randomUUID();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.REVIEWER,
                questioningId,
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
        UUID questioningId = UUID.randomUUID();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.REVIEWER,
                questioningId,
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
        UUID questioningId = UUID.randomUUID();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.REVIEWER,
                questioningId,
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
        UUID questioningId = UUID.randomUUID();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(habilitationRole,
                questioningId,
                userRoles,
                "user-id");

        //then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", UserRoles.INTERVIEWER})
    @NullSource
    @DisplayName("Should return true if role undefined and user is respondant and has accreditation")
    void should_return_true_if_role_undefined_and_user_is_respondant_and_has_accreditation(String habilitationRole) {
        //given
        List<String> userRoles = List.of("plop", "test", AuthorityRoleEnum.RESPONDENT.securityRole());
        QuestioningAccreditation accreditation = new QuestioningAccreditation();
        accreditation.setIdContact("user-id");
        Questioning questioning = new Questioning();
        UUID questioningId = UUID.randomUUID();
        questioning.setId(questioningId);
        accreditation.setQuestioning(questioning);
        questioningAccreditationServiceStub.setQuestioningAccreditationList(List.of(accreditation));

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(habilitationRole,
                questioningId,
                userRoles,
                "user-id");

        //then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", UserRoles.INTERVIEWER})
    @NullSource
    @DisplayName("Should return false if role undefined and user is respondant and has not accreditation")
    void should_return_false_if_role_undefined_and_user_is_respondant_and_has_not_accreditation(String habilitationRole) {
        //given
        List<String> userRoles = List.of("plop", "test", AuthorityRoleEnum.RESPONDENT.securityRole());
        UUID questioningId = UUID.randomUUID();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(habilitationRole,
                questioningId,
                userRoles,
                "user-id");

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true if habilitation role is EXPERT (interrogation with status Expertise) and user has internal user role")
    void should_return_true_if_habilitation_role_is_expert_and_user_has_internal_user_role() {
        //given
        List<String> userRoles = List.of(AuthorityRoleEnum.INTERNAL_USER.securityRole());
        String userId = "user-id";
        User user = new User();
        user.setIdentifier(userId);
        user.setRole(UserRoleTypeEnum.GESTIONNAIRE);
        userServiceStub.setUsers(List.of(user));
        UUID questioningId = UUID.randomUUID();
        Questioning questioning = new Questioning();
        questioning.setId(questioningId);
        questioning.setHighestTypeEvent(TypeQuestioningEvent.EXPERT);
        questioningService.saveQuestioning(questioning);

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.EXPERT,
                questioningId,
                userRoles,
                userId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false if habilitation role is empty and user has internal user role")
    void should_return_false_if_habilitation_role_is_empty_and_user_has_internal_user_role() {
        //given
        List<String> userRoles = List.of(AuthorityRoleEnum.INTERNAL_USER.securityRole());
        String userId = "user-id";
        User user = new User();
        user.setIdentifier(userId);
        user.setRole(UserRoleTypeEnum.GESTIONNAIRE);
        userServiceStub.setUsers(List.of(user));
        UUID questioningId = UUID.randomUUID();

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                null,
                questioningId,
                userRoles,
                userId);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false if habilitation role is EXPERT and user has no internal user role")
    void should_return_false_if_habilitation_role_is_EXPERT_and_user_has_no_internal_user_role() {
        //given
        List<String> userRoles = List.of(AuthorityRoleEnum.RESPONDENT.securityRole());
        String userId = "user-id";
        User user = new User();
        user.setIdentifier(userId);
        user.setRole(UserRoleTypeEnum.GESTIONNAIRE);
        userServiceStub.setUsers(List.of(user));
        UUID questioningId = UUID.randomUUID();
        Questioning questioning = new Questioning();
        questioning.setId(questioningId);
        questioning.setHighestTypeEvent(TypeQuestioningEvent.EXPERT);
        questioningService.saveQuestioning(questioning);

        //when
        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.EXPERT,
                questioningId,
                userRoles,
                userId);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseIfUserDoesNotExist() {
        UUID questioningId = UUID.randomUUID();
        String userId = "unknownUser";

        boolean result = checkHabilitationServiceImplOidc.checkHabilitation(
                UserRoles.EXPERT,
                questioningId,
                List.of(AuthorityRoleEnum.INTERNAL_USER.securityRole()),
                userId);
        assertThat(result).isFalse();
    }
}
