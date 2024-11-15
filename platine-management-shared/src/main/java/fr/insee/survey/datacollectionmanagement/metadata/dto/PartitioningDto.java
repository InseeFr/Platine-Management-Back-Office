package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class PartitioningDto {

    @NotBlank
    private String id;
    private String campaignId;
    private String label;
    private Date openingDate;
    private Date closingDate;
    private Date returnDate;
}
