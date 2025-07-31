package fr.insee.survey.datacollectionmanagement.questioning.domain;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterrogationEventOrder {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeQuestioningEvent status;

    private int eventOrder;
}
