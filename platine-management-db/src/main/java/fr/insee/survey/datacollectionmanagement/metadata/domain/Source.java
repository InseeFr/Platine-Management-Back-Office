package fr.insee.survey.datacollectionmanagement.metadata.domain;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.user.domain.SourceAccreditation;
import jakarta.persistence.*;
import lombok.*;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Source {

    @Id
    private String id;
    @Column(name = "technical-id",unique = true)
    private String technicalId;
    private String longWording;
    private String shortWording;
    private Boolean forceClose;
    private String messageSurveyOffline;
    private String messageInfoSurveyOffline;
    private String logo;
    private String storageTime;
    private String personalData;
    @NonNull
    @Enumerated(EnumType.STRING)
    private PeriodicityEnum periodicity;
    @NonNull
    private Boolean mandatoryMySurveys;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "source")
    private Set<Survey> surveys;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "source")
    private Set<SourceAccreditation> sourceAccreditations;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "support_id")
    private Support support;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Enumerated(EnumType.STRING)
    private Set<Parameters> params;

    // PrePersist method to fill the unique field with 's' and 4 random digits
    @PrePersist
    public void generatetechnicalId() {

        if (technicalId == null) {
            // Generate 4 random digits
            Random random = new Random();
            int randomDigits = 1000 + random.nextInt(9000); // To ensure 4 digits (from 1000 to 9999)

            // Assign the value to the unique field
            technicalId = "s" + randomDigits;
        }
    }

}
