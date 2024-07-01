package fr.insee.survey.datacollectionmanagement.user.dto;


import fr.insee.survey.datacollectionmanagement.user.validation.InternalUserRoleValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserDto{
    @NotBlank
    private String identifier;
    @InternalUserRoleValid
    private String role;
    private String name;
    private String firstName;
    private String organization;
    private List<String> accreditedSources;
    private Date creationDate;
    private String creationAuthor;


}

