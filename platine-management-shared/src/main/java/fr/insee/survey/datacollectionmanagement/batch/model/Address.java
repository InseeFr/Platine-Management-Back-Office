package fr.insee.survey.datacollectionmanagement.batch.model;

import fr.insee.modelefiliere.AddressDto;

public record Address(
        String streetNumber,
        String repetitionIndex,
        String streetType,
        String streetName,
        String addressSupplement,
        String cityName,
        String zipCode,
        String cedexCode,
        String cedexName,
        String specialDistribution,
        String countryCode,
        String countryName
) {
    public static Address fromFiliereAddress(AddressDto dto) {
        return new Address(
                dto.getStreetNumber(),
                dto.getRepetitionIndex(),
                dto.getStreetType(),
                dto.getStreetName(),
                dto.getAddressSupplement(),
                dto.getCityName(),
                dto.getZipCode(),
                dto.getCedexCode(),
                dto.getCedexName(),
                dto.getSpecialDistribution(),
                dto.getCountryCode(),
                dto.getCountryName()
        );
    }
}
