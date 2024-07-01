package fr.insee.survey.datacollectionmanagement.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("Contact")
public class ContactInformationsDto {

    @JsonProperty("Identity")
    private String identity;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("UsualCompanyName")
    private String usualCompanyName;

    @JsonProperty("Address")
    private AddressInformationsDto addressInformationsDto;
}
