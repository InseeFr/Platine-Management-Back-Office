package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CampaignEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "campaign_event_seq")
    private Long id;
    @NonNull
    private String type;
    @NonNull
    private Date date;

    @ManyToOne
    private Campaign campaign;

}
