package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idContact_index", columnList = "idContact"),
        @Index(name = "questioning_index", columnList = "questioning_id")
})
public class QuestioningAccreditation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questioning_accreditation_seq")
    private Long id;

    private boolean isMain;
    private Date creationDate;
    private String creationAuthor;
    @NonNull
    private String idContact;

    @ManyToOne
    private Questioning questioning;

    @Override
    public String toString() {
        return "QuestioningAccreditation [id=" + id + ", isMain=" + isMain + ", creationDate=" + creationDate
                + ", creationAuthor=" + creationAuthor + ", idContact=" + idContact + "]";
    }

}
