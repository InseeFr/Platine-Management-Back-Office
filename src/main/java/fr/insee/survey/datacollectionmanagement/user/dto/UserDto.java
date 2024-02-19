package fr.insee.survey.datacollectionmanagement.user.dto;


import fr.insee.survey.datacollectionmanagement.user.validation.UserRoleValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserDto{
    @NotBlank
    private String identifier;
    @UserRoleValid
    private String role;
    private String name;
    private String firstName;
    private String Organization;
    private Date creationDate;
    private String creationAuthor;


}

