package fr.insee.survey.datacollectionmanagement.query.dto;

import java.io.Serializable;

public class MoogFollowUpDto implements Serializable {
    private int nb;
    private int freq;
    private String batchNum;

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }


    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }
}
