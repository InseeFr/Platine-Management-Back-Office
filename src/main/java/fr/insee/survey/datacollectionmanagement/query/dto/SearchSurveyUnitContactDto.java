package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchSurveyUnitContactDto {

    private String identifier;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String phoneNumber;
    private List<String> listSourcesId;
}
