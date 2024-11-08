package fr.insee.survey.datacollectionmanagement.view.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "view_identifier_index", columnList = "identifier"),
        @Index(name = "view_campaignId_index", columnList = "campaignId"),
        @Index(name = "view_idSu_index", columnList = "idSu")
})
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "view_seq")
    private Long id;
    @NonNull
    private String identifier;
    private String campaignId;
    private String idSu;
}
