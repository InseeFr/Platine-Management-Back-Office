package fr.insee.survey.datacollectionmanagement.query.validation;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleValidatorTest {

    private UserRoleValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserRoleValidator();
    }

    @Test
    void shouldAcceptNullRole() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void shouldAcceptEmptyRole() {
        assertThat(validator.isValid("", null)).isTrue();
    }

    @Test
    void shouldAcceptBlankRole() {
        assertThat(validator.isValid("   ", null)).isTrue();
    }

    @Test
    void shouldAcceptInterviewerRole() {
        assertThat(validator.isValid(UserRoles.INTERVIEWER, null)).isTrue();
    }

    @Test
    void shouldAcceptReviewerRole() {
        assertThat(validator.isValid(UserRoles.REVIEWER, null)).isTrue();
    }

    @Test
    void shouldAcceptExpertRole() {
        assertThat(validator.isValid(UserRoles.EXPERT, null)).isTrue();
    }

    @Test
    void shouldRejectUnknownRole() {
        assertThat(validator.isValid("ADMIN", null)).isFalse();
    }
}