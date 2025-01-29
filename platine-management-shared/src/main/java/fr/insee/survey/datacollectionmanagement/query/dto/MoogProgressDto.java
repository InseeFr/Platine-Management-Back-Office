package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MoogProgressDto implements Serializable {
    private int nbSu;
    private String batchNumber;
    private int nbIntReceived;
    private int nbPapReceived;
    private int nbPND;
    private int nbHC;
    private int nbRefusal;
    private int nbOtherWastes;
    private int nbIntPart;

    public MoogProgressDto(String batchNumber) {
        this.batchNumber = batchNumber;
        this.nbIntReceived = 0;
        this.nbPapReceived = 0;
        this.nbPND = 0;
        this.nbHC = 0;
        this.nbRefusal = 0;
        this.nbOtherWastes = 0;
        this.nbIntPart = 0;
    }
}
