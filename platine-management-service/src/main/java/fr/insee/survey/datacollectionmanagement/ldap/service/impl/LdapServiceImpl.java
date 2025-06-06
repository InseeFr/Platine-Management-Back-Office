package fr.insee.survey.datacollectionmanagement.ldap.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapAccreditationDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactInputDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactOutputDto;
import fr.insee.survey.datacollectionmanagement.ldap.service.LdapService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class LdapServiceImpl implements LdapService {

    private final RestTemplate ldapRestTemplate;
    public static final String PATH_SLASH = "/";
    public static final String REALMS_PATH = PATH_SLASH + "v2" + PATH_SLASH + "realms";
    public static final String STORAGES_PATH = PATH_SLASH + "storages";
    public static final String CONTACT_PATH = PATH_SLASH + "users";

    @Value("${fr.insee.datacollectionmanagement.ldap.api.realm}")
    String realm;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.storage}")
    String storage;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.accreditation.id}")
    String accreditationId;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.accreditation.application}")
    String accreditationApplication;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.accreditation.role}")
    String accreditationRole;

    @Value("${fr.insee.datacollectionmanagement.ldap.api.accreditation.property}")
    String accreditationProperty;

    @Override
    public ContactDto createUser(ContactDto contact) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("realm", realm);
        headers.set("storage", storage);
        headers.set("X-SUGOI-ASYNCHRONOUS-ALLOWED-REQUEST", "false");
        headers.setContentType(MediaType.APPLICATION_JSON);

        LdapAccreditationDto ldapAccreditationDto = new LdapAccreditationDto();
        ldapAccreditationDto.setApplication(accreditationApplication);
        ldapAccreditationDto.setProperty(accreditationProperty);
        ldapAccreditationDto.setRole(accreditationRole);
        ldapAccreditationDto.setId(accreditationId);
        LdapContactInputDto ldapContact = new LdapContactInputDto();
        ldapContact.setHabilitations(List.of(ldapAccreditationDto));

        String path = REALMS_PATH + PATH_SLASH + realm + STORAGES_PATH + PATH_SLASH + storage + CONTACT_PATH;
        HttpEntity<LdapContactInputDto> requestEntity = new HttpEntity<>(ldapContact, headers);

        ResponseEntity<LdapContactOutputDto> response = ldapRestTemplate.postForEntity(
                path,
                requestEntity,
                LdapContactOutputDto.class
        );

        String username = Objects.requireNonNull(response.getBody()).getUsername();
        contact.setIdentifier(username);

        return contact;
    }
}
