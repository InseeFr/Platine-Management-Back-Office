package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
public class Parameters {

    @Getter
    public enum ParameterEnum {
        URL_REDIRECTION,URL_TYPE, MAIL_ASSISTANCE;
    }

    @Id
    private String metadataId;

    @Id
    @Enumerated(EnumType.STRING)
    private ParameterEnum paramId;

    @Column(length = 2000)
    private String paramValue;

}
