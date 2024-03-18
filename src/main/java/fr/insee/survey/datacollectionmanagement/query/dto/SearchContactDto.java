package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchContactDto {

    private String identifier;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String function;
    private List<String> listSurveyUnitNames;
    private List<String> listSourcesId;
}
