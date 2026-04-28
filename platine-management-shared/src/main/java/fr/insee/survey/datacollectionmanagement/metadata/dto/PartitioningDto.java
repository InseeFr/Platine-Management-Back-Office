package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class PartitioningDto {

    @NotBlank
    private String id;
    private String campaignId;
    private String label;
        private Instant openingDate;
        private Instant closingDate;
        private Instant returnDate;
        private Instant openingLetterDate;
        private Instant openingMailDate;
        private Instant followupLetter1Date;
        private Instant followupLetter2Date;
        private Instant followupLetter3Date;
        private Instant followupLetter4Date;
        private Instant followupMail1Date;
        private Instant followupMail2Date;
        private Instant followupMail3Date;
        private Instant followupMail4Date;
        private Instant formalNoticeDate;
        private Instant noReplyDate;
}
