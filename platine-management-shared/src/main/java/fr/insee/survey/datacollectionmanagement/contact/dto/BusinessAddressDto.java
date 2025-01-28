package fr.insee.survey.datacollectionmanagement.contact.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessAddressDto {
    private String libellePays;
    private String numeroVoie;
    private String indiceRepetition;
    private String typeVoie;
    private String libelleVoie;
    private String complementAdresse;
    private String mentionSpeciale;
    private String codePostal;
    private String libelleCommune;
    private String bureauDistributeur;
    private String codeCedex;
    private String libelleCedex;
    private String codeCommune;
}
