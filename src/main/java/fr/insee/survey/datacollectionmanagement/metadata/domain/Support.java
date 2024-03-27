package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Support {

    @Id
    private String id;

    private String label;
    private String phoneNumber;
    private String mail;
    private String countryName;
    private String streetNumber;
    private String streetName;
    private String city;
    private String zipCode;

    @OneToMany
    private Set<Source> sources;

}
