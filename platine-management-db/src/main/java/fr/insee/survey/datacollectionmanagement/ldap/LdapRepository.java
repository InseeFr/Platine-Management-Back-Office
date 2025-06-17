package fr.insee.survey.datacollectionmanagement.ldap;
import fr.insee.survey.datacollectionmanagement.contact.dto.LdapContactOutputDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface LdapRepository {
    ResponseEntity<LdapContactOutputDto> createContact();
}
