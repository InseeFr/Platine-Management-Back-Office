package fr.insee.survey.datacollectionmanagement.metadata.domain;

import fr.insee.survey.datacollectionmanagement.metadata.enums.ParameterEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Parameters {

    @Id
    private String metadataId;

    @Id
    @Enumerated(EnumType.STRING)
    private ParameterEnum paramId;

    @Column(length = 2000)
    private String paramValue;

}
