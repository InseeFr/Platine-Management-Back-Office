package fr.insee.survey.datacollectionmanagement.contact.dto;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
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
    private Contact.Gender civility;
    private String lastName;
    private String firstName;
    private String function;
    private String email;
    private String phone;
    private AddressDto address;

}
