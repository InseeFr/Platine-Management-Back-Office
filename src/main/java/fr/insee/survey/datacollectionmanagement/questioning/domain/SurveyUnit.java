package fr.insee.survey.datacollectionmanagement.questioning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(indexes = {
    @Index(name = "identificationName_index", columnList = "identificationName"),
        @Index(name = "identificationCode_index", columnList = "identificationCode"),
        @Index(name = "surveyUnitAddress_index", columnList = "survey_unit_address_id")
})
public class SurveyUnit {

    @Id
    private String idSu;

    @OneToMany
    private Set<Questioning> questionings;

    private String identificationCode;

    // "Raison Sociale"
    private String identificationName;

    private String label;


    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private SurveyUnitAddress surveyUnitAddress;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SurveyUnitComment> surveyUnitComments;

    @Override
    public String toString() {
        return "SurveyUnit [idSu=" + idSu + ", identificationCode=" + identificationCode + ", identificationName="
                + identificationName + ", surveyUnitAddress=" + surveyUnitAddress + "]";
    }
    
    

}
