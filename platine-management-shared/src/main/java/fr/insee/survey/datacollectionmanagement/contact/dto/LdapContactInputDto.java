package fr.insee.survey.datacollectionmanagement.contact.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LdapContactInputDto {
    List<LdapAccreditationDto> habilitations;
}

