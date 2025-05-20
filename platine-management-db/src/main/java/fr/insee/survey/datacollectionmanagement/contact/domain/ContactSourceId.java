package fr.insee.survey.datacollectionmanagement.contact.domain;

import lombok.*;
import java.io.Serializable;
import jakarta.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@Getter
@Setter
public class ContactSourceId implements Serializable {
    private String sourceId;
    private String contactId;
    private String surveyUnitId;
}