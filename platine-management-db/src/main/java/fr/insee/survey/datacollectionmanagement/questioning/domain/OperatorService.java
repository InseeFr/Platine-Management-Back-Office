package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
public class OperatorService {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operator_service_seq")
    private Long id;
    private String name;
    private String mail;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "operatorService")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Operator> operators;
}
