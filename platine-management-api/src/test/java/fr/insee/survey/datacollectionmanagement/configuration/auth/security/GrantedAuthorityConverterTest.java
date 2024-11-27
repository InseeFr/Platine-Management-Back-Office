package fr.insee.survey.datacollectionmanagement.configuration.auth.security;

import fr.insee.survey.datacollectionmanagement.configuration.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GrantedAuthorityConverterTest {

    private GrantedAuthorityConverter converter;

    private ApplicationConfig applicationConfig;

    private static final List<String> JWT_ROLE_INTERNAL_USER = List.of("internal_user", "intern_user");
    private static final List<String> JWT_ROLE_RESPONDENT = List.of("respondent", "resp");
    private static final List<String> JWT_ROLE_ADMIN = List.of("admin", "adm");
    private static final List<String> JWT_ROLE_WEBCLIENT = List.of("webclient","webcli");

    @BeforeEach
    void init() {
        applicationConfig = new ApplicationConfig();
    }

    @Test
    @DisplayName("Given a JWT, when converting null or empty JWT role, then converting ignore these roles")
    void testConverter01() {
        applicationConfig.setRoleAdmin(List.of(""));
        applicationConfig.setRoleInternalUser(List.of());
        applicationConfig.setRoleWebClient(List.of(""));
        List<String> nullList = new ArrayList<>();
        nullList.add(null);
        applicationConfig.setRoleRespondent(nullList);
        converter = new GrantedAuthorityConverter(applicationConfig);
        List<String> tokenRoles = new ArrayList<>();
        tokenRoles.add(null);
        tokenRoles.add("");

        Jwt jwt = createJwt(tokenRoles);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Given a JWT, when converting roles, then convert only JWT roles matching roles in role properties")
    void testConverter02() {
        applicationConfig.setRoleAdmin(JWT_ROLE_ADMIN);
        applicationConfig.setRoleInternalUser(JWT_ROLE_INTERNAL_USER);
        applicationConfig.setRoleWebClient(List.of("webclient", "adm"));
        applicationConfig.setRoleRespondent(JWT_ROLE_RESPONDENT);
        converter = new GrantedAuthorityConverter(applicationConfig);
        List<String> tokenRoles = List.of("dummyRole1", "internal_user", "dummyRole2", "webclient", "adm");

        Jwt jwt = createJwt(tokenRoles);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority(AuthorityRoleEnum.INTERNAL_USER.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.ADMIN.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.WEB_CLIENT.securityRole()));
    }

    @Test
    @DisplayName("Given a JWT, when converting roles, then accept a config role can be used for multiple app roles")
    void testConverter03() {
        String dummyRole = "dummyRole";
        String dummyRole2 = "dummyRole2";
        applicationConfig.setRoleAdmin(List.of(dummyRole));
        applicationConfig.setRoleInternalUser(List.of(dummyRole2));
        applicationConfig.setRoleWebClient(List.of(dummyRole));
        applicationConfig.setRoleRespondent(List.of(""));
        converter = new GrantedAuthorityConverter(applicationConfig);

        List<String> tokenRoles = List.of(dummyRole, "role-not-used", dummyRole2, "role-not-used-2");
        Jwt jwt = createJwt(tokenRoles);

        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(3)
                .contains(
                        new SimpleGrantedAuthority(AuthorityRoleEnum.INTERNAL_USER.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.ADMIN.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRoleEnum.WEB_CLIENT.securityRole()));
    }

    @ParameterizedTest
    @MethodSource("provideJWTRoleWithAppRoleAssociated")
    @DisplayName("Given a JWT, when converting roles, then assure each JWT role is converted to equivalent app role")
    void testConverter04(List<String> jwtRoles, AuthorityRoleEnum appRole) {
        applicationConfig.setRoleAdmin(JWT_ROLE_ADMIN);
        applicationConfig.setRoleInternalUser(JWT_ROLE_INTERNAL_USER);
        applicationConfig.setRoleWebClient(JWT_ROLE_WEBCLIENT);
        applicationConfig.setRoleRespondent(JWT_ROLE_RESPONDENT);
        converter = new GrantedAuthorityConverter(applicationConfig);

        Jwt jwt = createJwt(jwtRoles);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(1)
                .contains(new SimpleGrantedAuthority(appRole.securityRole()));
    }

    private static Stream<Arguments> provideJWTRoleWithAppRoleAssociated() {
        return Stream.of(
                Arguments.of(JWT_ROLE_INTERNAL_USER, AuthorityRoleEnum.INTERNAL_USER),
                Arguments.of(JWT_ROLE_ADMIN, AuthorityRoleEnum.ADMIN),
                Arguments.of(JWT_ROLE_WEBCLIENT, AuthorityRoleEnum.WEB_CLIENT),
                Arguments.of(JWT_ROLE_RESPONDENT, AuthorityRoleEnum.RESPONDENT));
    }

    private Jwt createJwt(List<String> tokenRoles) {
        Map<String, Object> jwtHeaders = new HashMap<>();
        jwtHeaders.put("header", "headerValue");

        Map<String, Object> claims = new HashMap<>();
        claims.put(applicationConfig.getRoleClaim(), tokenRoles);

        return new Jwt("user-id", Instant.now(), Instant.MAX, jwtHeaders, claims);
    }
}
