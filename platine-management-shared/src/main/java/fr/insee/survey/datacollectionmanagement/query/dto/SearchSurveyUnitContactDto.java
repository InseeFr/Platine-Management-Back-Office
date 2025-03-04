package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class SearchSurveyUnitContactDto {

    private String identifier;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String phoneNumber;
    private String function;
    private Set<String> campaigns;
}
