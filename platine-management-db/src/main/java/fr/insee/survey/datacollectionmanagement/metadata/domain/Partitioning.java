package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
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

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;

}
