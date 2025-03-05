package fr.insee.survey.datacollectionmanagement.questioning.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operator_seq")
    private Long id;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_service_id")
    private OperatorService operatorService;


}
