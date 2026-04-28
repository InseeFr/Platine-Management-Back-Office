package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "campainId_index", columnList = "campaign_id")
})
public class Partitioning {

    @Id
    private String id;
    private String label;
    private Instant openingDate;
    private Instant closingDate;
    private Instant returnDate;
    private Instant openingLetterDate;
    private Instant openingMailDate;
    @Column(name = "followup_letter_1_date")
    private Instant followupLetter1Date;
    @Column(name = "followup_letter_2_date")
    private Instant followupLetter2Date;
    @Column(name = "followup_letter_3_date")
    private Instant followupLetter3Date;
    @Column(name = "followup_letter_4_date")
    private Instant followupLetter4Date;
    @Column(name = "followup_mail_1_date")
    private Instant followupMail1Date;
    @Column(name = "followup_mail_2_date")
    private Instant followupMail2Date;
    @Column(name = "followup_mail_3_date")
    private Instant followupMail3Date;
    @Column(name = "followup_mail_4_date")
    private Instant followupMail4Date;
    private Instant formalNoticeDate;
    private Instant noReplyDate;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;

}
