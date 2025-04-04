package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "surveyyear_index", columnList = "year_value"),
        @Index(name = "source_index", columnList = "source_id")
})
public class Survey {

    @Id
    private String id;
    @Column(name = "YEAR_VALUE")
    @NonNull
    private Integer year;
    private Integer sampleSize;
    @Column(length = 2000)
    private String longWording;
    @Column(length = 2000)
    private String shortWording;
    @Column(length = 2000)
    private String shortObjectives;
    @Column(length = 2000)
    private String longObjectives;
    private String visaNumber;
    private String cnisUrl;
    private String diffusionUrl;
    private String noticeUrl;
    private String specimenUrl;
    private String communication;
    @Column(columnDefinition = "boolean default false")
    private boolean compulsoryNature;
    private String rgpdBlock;
    private String sendPaperQuestionnaire;
    @Column(columnDefinition = "boolean default false")
    private boolean reExpedition;
    private String managementApplicationName;
    @Column(columnDefinition = "boolean default false")
    private boolean contactExtraction;
    private String contactExtractionNb;
    private String surveyStatus;
    @Column(columnDefinition = "boolean default false")
    private boolean sviUse;
    private String sviNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "survey")
    private Set<Campaign> campaigns;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private Source source;

}
