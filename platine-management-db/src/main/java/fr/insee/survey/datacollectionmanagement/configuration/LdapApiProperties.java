package fr.insee.survey.datacollectionmanagement.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

@ConfigurationProperties(prefix = "fr.insee.datacollectionmanagement.ldap.api")
public record LdapApiProperties(
        String realm,
        String storage,

        @Name("accreditation.application")
        String accreditationApplication,

        @Name("accreditation.role")
        String accreditationRole
) {}
