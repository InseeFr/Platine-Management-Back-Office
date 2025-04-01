package fr.insee.survey.datacollectionmanagement.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorityRoleEnumTest {

    @Test
    void testSecurityRole() {
        assertEquals("ROLE_ADMIN", AuthorityRoleEnum.ADMIN.securityRole());
        assertEquals("ROLE_WEB_CLIENT", AuthorityRoleEnum.WEB_CLIENT.securityRole());
        assertEquals("ROLE_INTERNAL_USER", AuthorityRoleEnum.INTERNAL_USER.securityRole());
        assertEquals("ROLE_RESPONDENT", AuthorityRoleEnum.RESPONDENT.securityRole());
        assertEquals("ROLE_PORTAL", AuthorityRoleEnum.PORTAL.securityRole());
    }
}