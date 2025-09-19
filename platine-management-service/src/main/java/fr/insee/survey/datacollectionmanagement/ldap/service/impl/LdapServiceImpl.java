package fr.insee.survey.datacollectionmanagement.ldap.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactOutputDto;
import fr.insee.survey.datacollectionmanagement.ldap.LdapRepository;
import fr.insee.survey.datacollectionmanagement.ldap.service.LdapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class LdapServiceImpl implements LdapService {

    private final LdapRepository ldapRepository;

    @Override
    public ContactDto createUser(ContactDto contact) {
        ResponseEntity<LdapContactOutputDto> ldapContactOutputDtoResponseEntity = ldapRepository.createContact();
        String username = Objects.requireNonNull(ldapContactOutputDtoResponseEntity.getBody()).getUsername();
        contact.setIdentifier(username);
        log.info("Contact created : {}", username);
        return contact;
    }
}
