package fr.insee.survey.datacollectionmanagement.user.dto;


import fr.insee.survey.datacollectionmanagement.user.validation.InternalUserRoleValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto{
    @NotBlank
    private String identifier;
    @InternalUserRoleValid
    private String role;

}

