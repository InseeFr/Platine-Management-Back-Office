package fr.insee.survey.datacollectionmanagement.contact.dto;

import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.validation.ContactGenderValid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContactDetailsDto {

    private String identifier;
    private String externalId;
    @ContactGenderValid
    private GenderEnum civility;
    private String lastName;
    private String firstName;
    private String function;
    private String email;
    private String phone;
    private String otherPhone;
    private String usualCompanyName;
    private AddressDto address;
    private List<String> listCampaigns;

}
