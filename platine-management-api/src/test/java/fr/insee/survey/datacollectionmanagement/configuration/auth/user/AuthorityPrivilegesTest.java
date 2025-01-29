package fr.insee.survey.datacollectionmanagement.configuration.auth.user;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class AuthorityPrivilegesTest {
    @Test
    void testConstructorIsPrivate() throws Exception {
        //uses reflexion to call private constructor
        //Given
        java.lang.reflect.Constructor<AuthorityPrivileges> constructor = AuthorityPrivileges.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        //When+Then ==> Assert that the private constructor throws
        try {
            constructor.newInstance();
            fail("Expected IllegalArgumentException to be thrown");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertInstanceOf(IllegalArgumentException.class, cause);
            assertEquals("Constant class", cause.getMessage());
        }
    }

}