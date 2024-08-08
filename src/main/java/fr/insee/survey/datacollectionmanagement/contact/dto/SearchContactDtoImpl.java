package fr.insee.survey.datacollectionmanagement.contact.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchContactDtoImpl implements SearchContactDto{

    private String identifier;
    private String email;
    private String firstName;
    private String lastName;
}
