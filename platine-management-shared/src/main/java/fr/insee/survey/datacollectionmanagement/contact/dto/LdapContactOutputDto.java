package fr.insee.survey.datacollectionmanagement.contact.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LdapContactOutputDto {
    private String username;
    List<LdapAccreditationDto> habilitations;
}
