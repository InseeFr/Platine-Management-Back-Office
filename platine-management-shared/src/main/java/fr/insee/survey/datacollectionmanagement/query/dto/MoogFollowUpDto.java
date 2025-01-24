package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class MoogFollowUpDto implements Serializable {
    private int nb;
    private int freq;
    private String batchNum;

    public MoogFollowUpDto(int nb, int freq, String batchNum) {
        this.nb = nb;
        this.freq = freq;
        this.batchNum = batchNum;
    }
}
