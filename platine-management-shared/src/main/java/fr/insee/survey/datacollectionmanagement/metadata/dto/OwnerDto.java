package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OwnerDto {

    @NotBlank
    private String id;
    private String label;
    private String ministry;
    private String logo;
    private String determiner;
    private String signatoryFunction;
    private String signatoryName;

}
