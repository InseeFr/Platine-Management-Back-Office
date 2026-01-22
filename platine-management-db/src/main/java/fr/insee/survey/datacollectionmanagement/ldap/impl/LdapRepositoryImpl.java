package fr.insee.survey.datacollectionmanagement.ldap.impl;

import fr.insee.survey.datacollectionmanagement.configuration.LdapApiProperties;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapAccreditationDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactInputDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactOutputDto;
import fr.insee.survey.datacollectionmanagement.ldap.LdapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LdapRepositoryImpl implements LdapRepository {

    private final RestClient restClient;

    public static final String PATH_SLASH = "/";
    public static final String REALMS_PATH = PATH_SLASH + "v2" + PATH_SLASH + "realms";
    public static final String STORAGES_PATH = PATH_SLASH + "storages";
    public static final String CONTACT_PATH = PATH_SLASH + "users";

    private final LdapApiProperties properties;

    @Override
    public ResponseEntity<LdapContactOutputDto> createContact()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("realm", properties.realm());
        headers.set("storage", properties.storage());
        headers.set("X-SUGOI-ASYNCHRONOUS-ALLOWED-REQUEST", "false");
        headers.setContentType(MediaType.APPLICATION_JSON);

        LdapAccreditationDto ldapAccreditationDto = new LdapAccreditationDto();
        ldapAccreditationDto.setApplication(properties.accreditationApplication());
        ldapAccreditationDto.setRole(properties.accreditationRole());
        LdapContactInputDto ldapContact = new LdapContactInputDto();
        ldapContact.setHabilitations(List.of(ldapAccreditationDto));

        String path = REALMS_PATH + PATH_SLASH + properties.realm() +
                STORAGES_PATH + PATH_SLASH + properties.storage() + CONTACT_PATH;

        return restClient.post()
                .uri(path)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(ldapContact)
                .retrieve()
                .toEntity(LdapContactOutputDto.class);
    }
}
