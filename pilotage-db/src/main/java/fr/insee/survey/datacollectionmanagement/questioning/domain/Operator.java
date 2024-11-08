package fr.insee.survey.datacollectionmanagement.questioning.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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


}
