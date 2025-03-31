package fr.insee.survey.datacollectionmanagement.metadata.domain;

import jakarta.persistence.*;
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
    private String signatoryName;
    private String signatoryFunction;
    @Column(name = "address_line_1")
    private String  addressLine1;
    @Column(name = "address_line_2")
    private String  addressLine2;
    @Column(name = "address_line_3")
    private String  addressLine3;
    @Column(name = "address_line_4")
    private String  addressLine4;
    @Column(name = "address_line_5")
    private String  addressLine5;
    @Column(name = "address_line_6")
    private String  addressLine6;
    @Column(name = "address_line_7")
    private String  addressLine7;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "support")
    private Set<Source> sources;

}
