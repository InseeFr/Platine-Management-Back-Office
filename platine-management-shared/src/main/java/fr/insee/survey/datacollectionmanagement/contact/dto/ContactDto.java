package fr.insee.survey.datacollectionmanagement.contact.dto;

import fr.insee.survey.datacollectionmanagement.contact.validation.ContactGenderValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactDto{

    @NotBlank(message = "Id can't be empty")
    private String identifier;
    private String externalId;
    @ContactGenderValid
    private String civility;
    private String lastName;
    private String firstName;
    private String function;
    private String email;
    private String phone;
    private String otherPhone;
    private String usualCompanyName;
    private AddressDto address;

}
