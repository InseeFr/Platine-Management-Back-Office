package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class PartitioningDto {

    @NotBlank
    private UUID id;
    private UUID campaignId;
    private String campaignShortWording;
    private String shortWording;
    private String label;
    private Date openingDate;
    private Date closingDate;
    private Date returnDate;
}
