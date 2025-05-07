package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.Entity;
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

    private String status;

    private int eventOrder;
}
