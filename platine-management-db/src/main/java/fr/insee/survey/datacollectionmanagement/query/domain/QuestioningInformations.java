package fr.insee.survey.datacollectionmanagement.query.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class QuestioningInformations {

    private String identifier;
    private String gender;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String phone2;
    private String usualCompanyName;
    private String countryName;
    private String streetName;
    private String streetNumber;
    private String zipCode;
    private String cityName;
    private String addressSupplement;
    private String specialDistribution;
    private String cedexCode;
    private String cedexName;
    private String repetitionIndex;
    private String streetType;
    private String label;
    private String idSu;
    private String identificationCode;
    private String identificationName;
    private String returnDate;
    private String logo;
    private UUID questioningId;
    private String sourceId;

}
