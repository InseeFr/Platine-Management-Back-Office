package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportDto {

    @NotBlank
    private String id;
    private String label;
    private String phoneNumber;
    private String mail;
    private String signatoryName;
    private String signatoryFunction;
    private String  addressLine1;
    private String  addressLine2;
    private String  addressLine3;
    private String  addressLine4;
    private String  addressLine5;
    private String  addressLine6;
    private String  addressLine7;
}
