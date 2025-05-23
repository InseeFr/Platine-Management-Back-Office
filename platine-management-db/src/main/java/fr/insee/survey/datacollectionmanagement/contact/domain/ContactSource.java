package fr.insee.survey.datacollectionmanagement.contact.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactSource {
    @EmbeddedId
    private ContactSourceId id;

    @Column(nullable = false)
    private boolean isMain;
}
