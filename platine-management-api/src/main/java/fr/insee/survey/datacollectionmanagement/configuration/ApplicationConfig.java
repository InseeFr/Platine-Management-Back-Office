package fr.insee.survey.datacollectionmanagement.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
@Setter
public class ApplicationConfig {

    //AUTHENTICATION
    @Value("${jwt.role-claim}")
    private String roleClaim;

    @Value("${jwt.id-claim}")
    private String idClaim;

    @Value("#{'${fr.insee.datacollectionmanagement.roles.admin.role}'.split(',')}")
    private List<String> roleAdmin;

    @Value("#{'${fr.insee.datacollectionmanagement.roles.webclient.role}'.split(',')}")
    private List<String> roleWebClient;

    @Value("#{'${fr.insee.datacollectionmanagement.roles.respondent.role}'.split(',')}")
    private List<String> roleRespondent;

    @Value("#{'${fr.insee.datacollectionmanagement.roles.internal.user.role}'.split(',')}")
    private List<String> roleInternalUser;

    @Value("#{'${fr.insee.datacollectionmanagement.roles.portal.role}'.split(',')}")
    private List<String> rolePortal;

    @Value("#{'${fr.insee.datacollectionmanagement.roles.reader.role}'.split(',')}")
    private List<String> roleReader;

    @Value("${fr.insee.datacollectionmanagement.auth.mode}")
    private String authType;

    @Value("${fr.insee.datacollectionmanagement.cors.allowedOrigins}")
    private String[] allowedOrigins;

    @Value("${fr.insee.datacollectionmanagement.auth.realm}")
    private String keycloakRealm;

    @Value("${fr.insee.datacollectionmanagement.auth.server-url}")
    private String keyCloakUrl;

    @Value("${fr.insee.datacollectionmanagement.api.lunatic.normal.url}")
    private String lunaticNormalUrl;

    @Value("${fr.insee.datacollectionmanagement.api.lunatic.sensitive.url}")
    private String lunaticSensitiveUrl;

    @Value("${fr.insee.datacollectionmanagement.api.xform1.url}")
    private String xform1Url;

    @Value("${fr.insee.datacollectionmanagement.api.xform2.url}")
    private String xform2Url;

    @Value("${fr.insee.datacollectionmanagement.api.questioning.api.url}")
    private String questionnaireApiUrl;

    @Value("${fr.insee.datacollectionmanagement.api.questioning.sensitive.api.url}")
    private String questionnaireApiSensitiveUrl;

    @Value("#{'${fr.insee.datacollectionmanagement.public.urls}'}")
    String[] publicUrls;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.url}")
    private String ldapApiUrl;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.accreditation.property}")
    private String ldapApiProperty;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.accreditation.role}")
    private String ldapApiRole;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.accreditation.id}")
    private String ldapApiId;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.accreditation.application}")
    private String ldapApiApplication;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.realm}")
    private String ldapApiRealm;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.storage}")
    private String ldapApiStorage;
}
