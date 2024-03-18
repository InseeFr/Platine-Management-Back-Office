package fr.insee.survey.datacollectionmanagement.contact.dto;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.validation.ContactGenderValid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactFirstLoginDto{

    private String identifier;
    private String externalId;
    @ContactGenderValid
    private Contact.Gender civility;
    private String lastName;
    private String firstName;
    private String function;
    private String email;
    private String phone;
    private String usualCompanyName;
    private boolean firstConnect;
    private AddressDto address;

}
