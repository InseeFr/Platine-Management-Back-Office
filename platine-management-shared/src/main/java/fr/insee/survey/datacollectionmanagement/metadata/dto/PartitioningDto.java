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
