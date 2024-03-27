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
public class Owner {

    @Id
    private String id;

    private String label;
    private String ministry;
    private String logo;

    @OneToMany
    private Set<Source> sources;

}