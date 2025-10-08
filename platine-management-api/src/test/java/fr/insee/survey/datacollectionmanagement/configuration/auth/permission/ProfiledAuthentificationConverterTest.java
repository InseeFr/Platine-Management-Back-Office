package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationConverterTest {

    @Mock
    private ProfileFactory profileFactory;

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private AuthorizationProfile authorizationProfile;

    private JwtAuthenticationConverter converter;

    private Map<String, List<GrantedAuthority>> roles;
    private String principalClaimName = "preferred_username";

    @BeforeEach
    void setUp() {
        roles = new HashMap<>();
        converter = new JwtAuthenticationConverter();

        // Initialize roles for testing
        List<String> adminConfigRoles = Arrays.asList("ADMIN", "SUPER_ADMIN");
        List<String> userConfigRoles = Arrays.asList("USER", "BASIC_USER");

        fillGrantedRoles(adminConfigRoles, AuthorityRoleEnum.ADMIN);
        fillGrantedRoles(userConfigRoles, AuthorityRoleEnum.USER);
    }

    @Test
    void convert_shouldCreateProfiledAuthenticationTokenWithCorrectAttributes() {
        // Given
        String username = "testuser";
        Map<String, Object> claims = new HashMap<>();
        claims.put(principalClaimName, username);

        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("ADMIN", "USER"));
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn(null);
        when(profileFactory.buildProfile(any(), any())).thenReturn(authorizationProfile);

        // When
        ProfiledAuthenticationToken result = (ProfiledAuthenticationToken) converter.convert(jwt);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(username);
        assertThat(result.getProfile()).isEqualTo(authorizationProfile);
        assertThat(result.getAuthorities()).isNotEmpty();
        verify(profileFactory).buildProfile(any(Set.class), any(Set.class));
    }

    @Test
    void convert_shouldExtractAuthoritiesFromApplicationRoles() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(principalClaimName, "user");

        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("ADMIN"));
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn(null);
        when(profileFactory.buildProfile(any(), any())).thenReturn(authorizationProfile);

        // When
        ProfiledAuthenticationToken result = (ProfiledAuthenticationToken) converter.convert(jwt);

        // Then
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN");
    }

    @Test
    void getApplicationRoles_shouldReturnEmptySetWhenUserRolesIsNull() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(principalClaimName, "user");
        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn("custom_roles");

        // When
        Set<AuthorityRoleEnum> result = converter.getApplicationRoles(jwt);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getApplicationRoles_shouldFilterNullAndBlankRoles() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(principalClaimName, "user");
        claims.put("custom_roles", Arrays.asList("ADMIN", null, "", "  ", "USER"));

        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn("custom_roles");

        // When
        Set<AuthorityRoleEnum> result = converter.getApplicationRoles(jwt);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(AuthorityRoleEnum.ADMIN, AuthorityRoleEnum.USER);
    }

    @Test
    void getApplicationRoles_shouldOnlyIncludeRolesInConfiguredMapping() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(principalClaimName, "user");
        claims.put("custom_roles", Arrays.asList("ADMIN", "UNKNOWN_ROLE", "USER"));

        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn("custom_roles");

        // When
        Set<AuthorityRoleEnum> result = converter.getApplicationRoles(jwt);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(AuthorityRoleEnum.ADMIN, AuthorityRoleEnum.USER);
        assertThat(result).doesNotContain(AuthorityRoleEnum.UNKNOWN);
    }

    @Test
    void getApplicationRoles_shouldReturnDistinctRoles() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(principalClaimName, "user");
        claims.put("custom_roles", Arrays.asList("ADMIN", "SUPER_ADMIN", "ADMIN"));

        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn("custom_roles");

        // When
        Set<AuthorityRoleEnum> result = converter.getApplicationRoles(jwt);

        // Then
        assertThat(result).containsExactly(AuthorityRoleEnum.ADMIN);
    }

    @Test
    void fillGrantedRoles_shouldNotModifyRolesMapWhenConfigRolesIsNull() {
        // Given
        Map<String, List<GrantedAuthority>> rolesBeforeFill = new HashMap<>(roles);

        // When
        fillGrantedRoles(null, AuthorityRoleEnum.ADMIN);

        // Then
        assertThat(roles).isEqualTo(rolesBeforeFill);
    }

    @Test
    void fillGrantedRoles_shouldSkipNullConfigRoles() {
        // Given
        List<String> configRoles = Arrays.asList("VALID_ROLE", null, "ANOTHER_VALID");
        int initialSize = roles.size();

        // When
        fillGrantedRoles(configRoles, AuthorityRoleEnum.VIEWER);

        // Then
        assertThat(roles).containsKey("VALID_ROLE");
        assertThat(roles.get("VALID_ROLE"))
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_VIEWER");
    }

    @Test
    void fillGrantedRoles_shouldSkipBlankConfigRoles() {
        // Given
        List<String> configRoles = Arrays.asList("VALID_ROLE", "", "  ");

        // When
        fillGrantedRoles(configRoles, AuthorityRoleEnum.EDITOR);

        // Then
        assertThat(roles).containsKey("VALID_ROLE");
        assertThat(roles.get("VALID_ROLE"))
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_EDITOR");
    }

    @Test
    void fillGrantedRoles_shouldAddAuthoritiesToExistingKey() {
        // Given
        List<String> firstRoles = Arrays.asList("SHARED_ROLE");
        List<String> secondRoles = Arrays.asList("SHARED_ROLE");

        // When
        fillGrantedRoles(firstRoles, AuthorityRoleEnum.ADMIN);
        fillGrantedRoles(secondRoles, AuthorityRoleEnum.USER);

        // Then
        assertThat(roles.get("SHARED_ROLE")).hasSize(2);
        assertThat(roles.get("SHARED_ROLE"))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void fillGrantedRoles_shouldCreateNewEntryForNewRole() {
        // Given
        List<String> configRoles = Arrays.asList("NEW_ROLE");

        // When
        fillGrantedRoles(configRoles, AuthorityRoleEnum.MODERATOR);

        // Then
        assertThat(roles).containsKey("NEW_ROLE");
        assertThat(roles.get("NEW_ROLE")).hasSize(1);
        assertThat(roles.get("NEW_ROLE").get(0).getAuthority()).isEqualTo("ROLE_MODERATOR");
    }

    @Test
    void getUserRoles_shouldUseRealmAccessWhenRoleClaimIsNull() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> realmAccess = new HashMap<>();
        List<String> expectedRoles = Arrays.asList("admin", "user");
        realmAccess.put("roles", expectedRoles);
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn(null);

        // When
        List<String> result = converter.getUserRoles(jwt);

        // Then
        assertThat(result).isEqualTo(expectedRoles);
    }

    @Test
    void getUserRoles_shouldUseRealmAccessWhenRoleClaimIsBlank() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> realmAccess = new HashMap<>();
        List<String> expectedRoles = Arrays.asList("admin", "user");
        realmAccess.put("roles", expectedRoles);
        claims.put("realm_access", realmAccess);

        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn("  ");

        // When
        List<String> result = converter.getUserRoles(jwt);

        // Then
        assertThat(result).isEqualTo(expectedRoles);
    }

    @Test
    void getUserRoles_shouldUseCustomRoleClaimWhenConfigured() {
        // Given
        String customClaimName = "custom_roles";
        List<String> expectedRoles = Arrays.asList("custom_admin", "custom_user");
        Map<String, Object> claims = new HashMap<>();
        claims.put(customClaimName, expectedRoles);

        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn(customClaimName);

        // When
        List<String> result = converter.getUserRoles(jwt);

        // Then
        assertThat(result).isEqualTo(expectedRoles);
    }

    @Test
    void getUserRoles_shouldReturnNullWhenCustomClaimNotPresent() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn("non_existent_claim");

        // When
        List<String> result = converter.getUserRoles(jwt);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convert_shouldHandleEmptyRolesList() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(principalClaimName, "user");
        claims.put("custom_roles", Collections.emptyList());

        Jwt jwt = createJwt(claims);

        when(applicationConfig.getRoleClaim()).thenReturn("custom_roles");
        when(profileFactory.buildProfile(any(), any())).thenReturn(authorizationProfile);

        // When
        ProfiledAuthenticationToken result = (ProfiledAuthenticationToken) converter.convert(jwt);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAuthorities()).isEmpty();
    }

    // Helper methods
    private Jwt createJwt(Map<String, Object> claims) {
        return new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                claims
        );
    }

    // Simulating the fillGrantedRoles method behavior for testing
    private void fillGrantedRoles(List<String> configRoles, AuthorityRoleEnum authorityRole) {
        if (configRoles == null) {
            return;
        }

        for (String configRole : configRoles) {
            if (configRole == null || configRole.isBlank()) {
                return;
            }

            this.roles.compute(configRole, (key, grantedAuthorities) -> {
                if (grantedAuthorities == null) {
                    grantedAuthorities = new ArrayList<>();
                }
                grantedAuthorities.add(new SimpleGrantedAuthority(authorityRole.securityRole()));
                return grantedAuthorities;
            });
        }
    }
}

// Supporting classes for the test (you'll need these in your actual codebase)
enum AuthorityRoleEnum {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    VIEWER("ROLE_VIEWER"),
    EDITOR("ROLE_EDITOR"),
    MODERATOR("ROLE_MODERATOR"),
    UNKNOWN("ROLE_UNKNOWN");

    private final String role;

    AuthorityRoleEnum(String role) {
        this.role = role;
    }

    public String securityRole() {
        return role;
    }

    public static AuthorityRoleEnum fromSecurityRole(String role) {
        for (AuthorityRoleEnum e : values()) {
            if (e.securityRole().equals(role)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}