package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "campainId_index", columnList = "campaign_id")
})
public class Partitioning {

    @Id
    private String id;
    @Column(name = "technical_id",unique = true)
    private UUID technicalId;
    private String label;
    private Date openingDate;
    private Date closingDate;
    private Date returnDate;
    private Date openingLetterDate;
    private Date openingMailDate;
    @Column(name = "followup_letter_1_date")
    private Date followupLetter1Date;
    @Column(name = "followup_letter_2_date")
    private Date followupLetter2Date;
    @Column(name = "followup_letter_3_date")
    private Date followupLetter3Date;
    @Column(name = "followup_letter_4_date")
    private Date followupLetter4Date;
    @Column(name = "followup_mail_1_date")
    private Date followupMail1Date;
    @Column(name = "followup_mail_2_date")
    private Date followupMail2Date;
    @Column(name = "followup_mail_3_date")
    private Date followupMail3Date;
    @Column(name = "followup_mail_4_date")
    private Date followupMail4Date;
    private Date formalNoticeDate;
    private Date noReplyDate;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;
}
