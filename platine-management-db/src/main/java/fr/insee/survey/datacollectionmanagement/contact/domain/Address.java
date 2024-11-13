package fr.insee.survey.datacollectionmanagement.contact.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    private Long id;

    private String streetNumber;
    private String repetitionIndex;
    private String streetType;
    private String streetName;
    private String addressSupplement;
    private String cityName;
    private String zipCode;
    private String cedexCode;
    private String cedexName;
    private String specialDistribution;
    private String countryCode;
    private String countryName;

    public String toStringMoog() {
        return (streetNumber!=null?streetNumber:"") +
                " "+ (repetitionIndex!=null?repetitionIndex:"")+
                " " +(streetType!=null?streetType:"")+
                "" + (streetName!=null?streetName:"")+
                " " +(addressSupplement!=null?addressSupplement:"") +
                " " + (cityName!=null?cityName:"")+
                " " + (zipCode!=null?zipCode:"") +
                " " + (cedexCode!=null?cedexCode:"") +
                " " + (cedexName!=null?cedexName:"") +
                " " + (specialDistribution!=null?specialDistribution:"")  +
                " " + (countryCode!=null?countryCode:"") +
                " " + (countryName!=null?countryName:"");
    }
}
