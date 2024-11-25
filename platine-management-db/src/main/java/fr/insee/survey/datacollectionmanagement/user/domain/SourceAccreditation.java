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

    @OneToOne
    private Source source;

    @Override
    public String toString() {
        return "SourceAccreditation{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", creationAuthor='" + creationAuthor + '\'' +
                ", idUser='" + idUser + '\'' +
                ", source=" + source +
                '}';
    }
}
