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
    void shouldAddReadSupportPermissionForSupportRole() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.SUPPORT),
                Set.of("SRC")
        );

        assertThat(profile.permissions())
                .containsExactly(Permission.READ_SUPPORT);
    }

    @Test
    void shouldAddReadPdfResponsePermissionForInternalUserRole() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(AuthorityRoleEnum.INTERNAL_USER),
                Set.of("SRC")
        );

        assertThat(profile.permissions())
                .containsExactly(Permission.READ_PDF_RESPONSE);
    }

    @Test
    void shouldAddBothPermissionsWhenBothRolesArePresent() {
        AuthorizationProfile profile = factory.buildProfile(
                Set.of(
                        AuthorityRoleEnum.SUPPORT,
                        AuthorityRoleEnum.INTERNAL_USER
                ),
                Set.of("SRC1")
        );

        assertThat(profile.permissions())
                .containsExactlyInAnyOrder(
                        Permission.READ_SUPPORT,
                        Permission.READ_PDF_RESPONSE
                );
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
