package fr.insee.survey.datacollectionmanagement.questioning.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EventOrder {

    @Id
    private Long id;

    private String status;

    private int eventOrder;

    public EventOrder(long id, String status, int eventOrder) {
        super();
        this.id = id;
        this.status = status;
        this.eventOrder = eventOrder;
    }

    public EventOrder() {
    }
}
