package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.AuthorizationProfile;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalPermissionCheckerTest {

    GlobalPermissionChecker checker = new GlobalPermissionChecker();

    @Mock
    Authentication authentication;

    @Mock
    ProfiledAuthenticationToken profiledAuthentication;

    @Mock
    AuthorizationProfile authorizationProfile;

    @Test
    void shouldReturnFalseWhenAuthenticationIsNull() {
        boolean result = checker.hasPermission(null, Permission.READ_SUPPORT);

        assertThat(result)
                .isFalse();
    }

    @Test
    void shouldReturnFalseWhenAuthenticationIsNotProfiledAuthenticationToken() {
        boolean result = checker.hasPermission(authentication, Permission.READ_SUPPORT);

        assertThat(result)
                .isFalse();
    }

    @Test
    void shouldReturnFalseWhenProfileDoesNotContainPermission() {
        when(profiledAuthentication.getProfile())
                .thenReturn(authorizationProfile);
        when(authorizationProfile.permissions())
                .thenReturn(Set.of(Permission.READ_PDF_RESPONSE));

        boolean result = checker.hasPermission(
                profiledAuthentication,
                Permission.READ_SUPPORT
        );

        assertThat(result)
                .isFalse();
    }

    @Test
    void shouldReturnTrueWhenProfileContainsPermission() {
        when(profiledAuthentication.getProfile())
                .thenReturn(authorizationProfile);
        when(authorizationProfile.permissions())
                .thenReturn(Set.of(Permission.READ_SUPPORT));

        boolean result = checker.hasPermission(
                profiledAuthentication,
                Permission.READ_SUPPORT
        );

        assertThat(result)
                .isTrue();
    }
}
