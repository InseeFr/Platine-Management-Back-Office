package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import lombok.Data;

@Data
public class ContactAccreditationDto {

    private String identifier;
    private String externalId;
    private boolean isMain;
    private GenderEnum civility;
    private String lastName;
    private String firstName;
    private String function;
    private String email;
    private String phone;
    private AddressDto address;

}
