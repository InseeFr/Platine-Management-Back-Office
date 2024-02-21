package fr.insee.survey.datacollectionmanagement.contact.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(indexes = {
        @Index(name = "fn_index", columnList = "firstName"), @Index(name = "ln_index", columnList = "lastName"),
        @Index(name = "lnfn_index", columnList = "lastName, firstName"),
        @Index(name = "email_index", columnList = "email"),
        @Index(name = "contactAddress_index", columnList = "address_id")

})
@Getter
@Setter
public class Contact {

    public enum Gender {
        Female, Male, Undefined
    }

    @Id
    private String identifier;
    
    private String externalId;
    private String lastName;
    private String firstName;
    private String email;
    private String function;
    private String phone;
    private String comment;
    @Column(columnDefinition = "boolean default false")
    private boolean emailVerify;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ContactEvent> contactEvents;

    @Enumerated(EnumType.STRING)
    private Gender gender;

}
