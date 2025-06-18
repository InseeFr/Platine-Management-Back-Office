package fr.insee.survey.datacollectionmanagement.contact.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LdapAccreditationDto {
    private String id;
    private String application;
    private String role;
    private String property;
}
