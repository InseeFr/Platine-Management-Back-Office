package fr.insee.survey.datacollectionmanagement.metadata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PeriodDto {
    private String value;
    private String label;
    private String period;

}
