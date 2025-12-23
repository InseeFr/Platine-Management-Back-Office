package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationProfileFactoryTest {

    AuthorizationProfileFactory factory = new AuthorizationProfileFactory();

    @Test
    void shouldBuildProfileWithNoPermissionsWhenNoRoles() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(),
                Set.of("SRC1", "SRC2")
        );

        assertThat(profile)
                .isNotNull();

        assertThat(profile.permissions())
                .isEmpty();

        assertThat(profile.sources())
                .containsExactlyInAnyOrder("SRC1", "SRC2");

        assertThat(profile.appRoles())
                .isEmpty();
    }

    @Test
    void shouldHaveValidPermissionForSupportRole() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.SUPPORT),
                Set.of("SRC")
        );

        assertThat(profile.permissions())
                .containsExactlyInAnyOrder(Permission.READ_SUPPORT);
    }

    @Test
    void shouldHaveValidPermissionForInternalUserRole() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.INTERNAL_USER),
                Set.of("SRC")
        );

        assertThat(profile.permissions()).isEmpty();
    }

    @Test
    void shouldHaveValidPermissionForAdmin() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.ADMIN),
                Set.of("SRC")
        );

        assertThat(profile.permissions())
                .containsExactlyInAnyOrder(Permission.READ_SUPPORT);
    }

    @Test
    void shouldHaveValidPermissionForRespondent() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.RESPONDENT),
                Set.of("SRC")
        );

        assertThat(profile.permissions()).isEmpty();
    }

    @Test
    void shouldHaveValidPermissionForPortal() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.PORTAL),
                Set.of("SRC")
        );

        assertThat(profile.permissions()).isEmpty();
    }

    @Test
    void shouldHaveValidPermissionForReader() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.READER),
                Set.of("SRC")
        );

        assertThat(profile.permissions()).isEmpty();
    }

    @Test
    void shouldHaveValidPermissionForWebClient() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.WEB_CLIENT),
                Set.of("SRC")
        );

        assertThat(profile.permissions()).isEmpty();
    }

    @Test
    void shouldPreserveSourcesAsIs() {
        Set<String> sources = Set.of("A", "B");

        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.SUPPORT),
                sources
        );

        assertThat(profile.sources())
                .isSameAs(sources);
    }
}
