package fr.insee.survey.datacollectionmanagement.contact.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessContactDto {
    private String ideC;
    private String adresseMessagerie;
    private String nom;
    private String prenom;
    private String fonction;
    private String raisonSocialeUsuelle;
    private String numeroTelephone;
    private String telephonePortable;
    private String facSimile;
    private String commentaire;
    private String ecivilite;
    @JsonProperty("adresse")
    private BusinessAddressDto businessAddressDto;
}
