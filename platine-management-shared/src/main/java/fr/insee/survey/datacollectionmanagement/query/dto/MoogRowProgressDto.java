package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MoogRowProgressDto {
    private int total;

    private String status;

    private String batchNum;

    public MoogRowProgressDto(int total, String status, String batchNum) {
        this.total = total;
        this.status = status;
        this.batchNum = batchNum;
    }
}
