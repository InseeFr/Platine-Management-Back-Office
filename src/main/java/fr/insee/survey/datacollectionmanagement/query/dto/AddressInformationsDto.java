package fr.insee.survey.datacollectionmanagement.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonRootName("Address")
public class AddressInformationsDto {

    @JsonProperty("StreetNumber")
    private String streetNumber;
    @JsonProperty("RepetitionIndex")
    private String repetitionIndex;
    @JsonProperty("StreetType")
    private String streetType;
    @JsonProperty("StreetName")
    private String streetName;
    @JsonProperty("AddressSupplement")
    private String addressSupplement;
    @JsonProperty("CityName")
    private String cityName;
    @JsonProperty("ZipCode")
    private String zipCode;
    @JsonProperty("CedexCode")
    private String cedexCode;
    @JsonProperty("CedexName")
    private String cedexName;
    @JsonProperty("SpecialDistribution")
    private String specialDistribution;
    @JsonProperty("CountryCode")
    private String countryCode;
    @JsonProperty("CountryName")
    private String countryName;

}
