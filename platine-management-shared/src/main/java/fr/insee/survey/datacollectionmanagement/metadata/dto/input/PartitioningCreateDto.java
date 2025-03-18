package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class PartitioningCreateDto {

    @NotBlank
    private String id;
    private UUID technicalId;
    private String campaignId;
    private String label;
    private Date openingDate;
    private Date closingDate;
    private Date returnDate;
    private Date openingLetterDate;
    private Date openingMailDate;
    private Date followupLetter1Date;
    private Date followupLetter2Date;
    private Date followupLetter3Date;
    private Date followupLetter4Date;
    private Date followupMail1Date;
    private Date followupMail2Date;
    private Date followupMail3Date;
    private Date followupMail4Date;
    private Date formalNoticeDate;
    private Date noReplyDate;
}
