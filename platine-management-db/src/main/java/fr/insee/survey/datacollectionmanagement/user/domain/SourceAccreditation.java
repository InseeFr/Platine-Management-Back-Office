package fr.insee.survey.datacollectionmanagement.user.domain;


import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SourceAccreditation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "source_accreditation_seq")
    private Long id;

    private Date creationDate;
    private String creationAuthor;
    @NonNull
    private String idUser;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private Source source;

}
