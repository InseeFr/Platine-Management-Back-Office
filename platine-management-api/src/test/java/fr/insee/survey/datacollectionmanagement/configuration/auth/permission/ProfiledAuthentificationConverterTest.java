package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.configuration.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfiledAuthenticationConverterTest {

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private AuthorizationProfileFactory profileFactory;

    private ProfiledAuthenticationConverter converter;

    private static final String PRINCIPAL_CLAIM_NAME = "preferred_username";

    @BeforeEach
    void setUp() {
        // Setup default application config
        when(applicationConfig.getRoleAdmin()).thenReturn(Arrays.asList("ROLE_ADMIN", "ADMIN"));
        when(applicationConfig.getRoleRespondent()).thenReturn(Arrays.asList("ROLE_RESPONDENT"));
        when(applicationConfig.getRoleInternalUser()).thenReturn(Arrays.asList("ROLE_INTERNAL_USER"));
        when(applicationConfig.getRoleWebClient()).thenReturn(Arrays.asList("ROLE_WEB_CLIENT"));
        when(applicationConfig.getRolePortal()).thenReturn(Arrays.asList("ROLE_PORTAL"));
        when(applicationConfig.getRoleReader()).thenReturn(Arrays.asList("ROLE_READER"));
        when(applicationConfig.getRoleSupport()).thenReturn(Arrays.asList("ROLE_SUPPORT"));
        when(applicationConfig.getRoleClaim()).thenReturn(null);

        // Setup profile factory to return a basic profile
        when(profileFactory.buildProfile(any(), any())).thenAnswer(invocation -> {
            Set<AuthorityRoleEnum> roles = invocation.getArgument(0);
            Set<String> sources = invocation.getArgument(1);
            return new AuthorizationProfile(roles, sources, new HashSet<>());
        });

        converter = new ProfiledAuthenticationConverter(applicationConfig, profileFactory, PRINCIPAL_CLAIM_NAME);
    }

    @Test
    void testConvert_withValidJwt_shouldReturnAuthenticationToken() {
        // Given
        String username = "testuser";
        Jwt jwt = createJwt(username, Arrays.asList("ROLE_ADMIN"));

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result).isNotNull().isInstanceOf(ProfiledAuthenticationToken.class);
        assertThat(result.getName()).isEqualTo(username);
        assertThat(result.getAuthorities()).isNotEmpty();
    }

    @Test
    void testConvert_withAdminRole_shouldGrantAdminAuthority() {
        // Given
        Jwt jwt = createJwt("admin", Arrays.asList("ROLE_ADMIN"));

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains(AuthorityRoleEnum.ADMIN.securityRole());
    }

    @Test
    void testConvert_withMultipleRoles_shouldGrantMultipleAuthorities() {
        // Given
        Jwt jwt = createJwt("user", Arrays.asList("ROLE_ADMIN", "ROLE_READER", "ROLE_SUPPORT"));

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).hasSize(3);
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder(
                        AuthorityRoleEnum.ADMIN.securityRole(),
                        AuthorityRoleEnum.READER.securityRole(),
                        AuthorityRoleEnum.SUPPORT.securityRole()
                );
    }

    @Test
    void testConvert_withDuplicateRoles_shouldReturnDistinctAuthorities() {
        // Given
        when(applicationConfig.getRoleAdmin()).thenReturn(Arrays.asList("ROLE_ADMIN", "ADMIN"));
        converter = new ProfiledAuthenticationConverter(applicationConfig, profileFactory, PRINCIPAL_CLAIM_NAME);
        Jwt jwt = createJwt("user", Arrays.asList("ROLE_ADMIN", "ADMIN"));

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains(AuthorityRoleEnum.ADMIN.securityRole());
    }

    @Test
    void testConvert_withNoMatchingRoles_shouldReturnEmptyAuthorities() {
        // Given
        Jwt jwt = createJwt("user", Arrays.asList("UNKNOWN_ROLE", "INVALID_ROLE"));

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void testConvert_withNullRolesInJwt_shouldReturnEmptyAuthorities() {
        // Given
        Jwt jwt = createJwtWithoutRoles("user");

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void testConvert_withEmptyRoles_shouldReturnEmptyAuthorities() {
        // Given
        Jwt jwt = createJwt("user", Collections.emptyList());

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void testConvert_withBlankRolesInList_shouldFilterThem() {
        // Given
        Jwt jwt = createJwt("user", Arrays.asList("ROLE_ADMIN", "", "  ", "ROLE_READER"));

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).hasSize(2);
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder(
                        AuthorityRoleEnum.ADMIN.securityRole(),
                        AuthorityRoleEnum.READER.securityRole()
                );
    }

    @Test
    void testConvert_withCustomRoleClaim_shouldExtractRolesFromCustomClaim() {
        // Given
        String customClaimName = "custom_roles";
        when(applicationConfig.getRoleClaim()).thenReturn(customClaimName);
        converter = new ProfiledAuthenticationConverter(applicationConfig, profileFactory, PRINCIPAL_CLAIM_NAME);

        Map<String, Object> claims = new HashMap<>();
        claims.put(PRINCIPAL_CLAIM_NAME, "user");
        claims.put(customClaimName, Arrays.asList("ROLE_ADMIN"));

        Jwt jwt = createJwtWithClaims(claims);

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains(AuthorityRoleEnum.ADMIN.securityRole());
    }

    @Test
    void testConvert_allRoleTypes_shouldMapCorrectly() {
        // Given
        Jwt jwt = createJwt("user", Arrays.asList(
                "ROLE_ADMIN",
                "ROLE_RESPONDENT",
                "ROLE_INTERNAL_USER",
                "ROLE_WEB_CLIENT",
                "ROLE_PORTAL",
                "ROLE_READER",
                "ROLE_SUPPORT"
        ));

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).hasSize(7);
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder(
                        AuthorityRoleEnum.ADMIN.securityRole(),
                        AuthorityRoleEnum.RESPONDENT.securityRole(),
                        AuthorityRoleEnum.INTERNAL_USER.securityRole(),
                        AuthorityRoleEnum.WEB_CLIENT.securityRole(),
                        AuthorityRoleEnum.PORTAL.securityRole(),
                        AuthorityRoleEnum.READER.securityRole(),
                        AuthorityRoleEnum.SUPPORT.securityRole()
                );
    }

    @Test
    void testFillGrantedRoles_withNullConfigRoles_shouldHandleGracefully() {
        // Given
        when(applicationConfig.getRoleAdmin()).thenReturn(null);

        // When/Then - Should not throw exception
        converter = new ProfiledAuthenticationConverter(applicationConfig, profileFactory, PRINCIPAL_CLAIM_NAME);
        Jwt jwt = createJwt("user", Arrays.asList("ROLE_ADMIN"));
        AbstractAuthenticationToken result = converter.convert(jwt);

        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void testFillGrantedRoles_withBlankConfigRole_shouldSkipIt() {
        // Given
        when(applicationConfig.getRoleAdmin()).thenReturn(Arrays.asList("ROLE_ADMIN", "", null));
        converter = new ProfiledAuthenticationConverter(applicationConfig, profileFactory, PRINCIPAL_CLAIM_NAME);

        Jwt jwt = createJwt("user", Arrays.asList("ROLE_ADMIN"));

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities()).hasSize(1);
    }

    @Test
    void testConvert_shouldPassRolesToProfileFactory() {
        // Given
        Jwt jwt = createJwt("user", Arrays.asList("ROLE_ADMIN", "ROLE_SUPPORT"));

        // When
        converter.convert(jwt);

        // Then - Verify profile factory was called (implementation specific)
        assertThat(((ProfiledAuthenticationToken) converter.convert(jwt)).getProfile()).isNotNull();
    }

    @Test
    void testConvert_withDifferentPrincipalClaimName_shouldExtractCorrectName() {
        // Given
        String customPrincipalClaim = "sub";
        converter = new ProfiledAuthenticationConverter(applicationConfig, profileFactory, customPrincipalClaim);

        Map<String, Object> claims = new HashMap<>();
        claims.put(customPrincipalClaim, "customUser");
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("ROLE_ADMIN"));
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwtWithClaims(claims);

        // When
        AbstractAuthenticationToken result = converter.convert(jwt);

        // Then
        assertThat(result.getName()).isEqualTo("customUser");
    }

    // Helper methods to create JWT mocks
    private Jwt createJwt(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(PRINCIPAL_CLAIM_NAME, username);

        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", roles);
        claims.put("realm_access", realmAccess);

        return createJwtWithClaims(claims);
    }

    private Jwt createJwtWithoutRoles(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(PRINCIPAL_CLAIM_NAME, username);

        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", null);
        claims.put("realm_access", realmAccess);

        return createJwtWithClaims(claims);
    }

    private Jwt createJwtWithClaims(Map<String, Object> claims) {
        return new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );
    }
}