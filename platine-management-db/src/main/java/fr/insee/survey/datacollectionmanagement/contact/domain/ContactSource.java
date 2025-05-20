package fr.insee.survey.datacollectionmanagement.contact.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
