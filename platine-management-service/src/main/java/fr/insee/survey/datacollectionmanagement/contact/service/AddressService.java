package fr.insee.survey.datacollectionmanagement.contact.service;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {

    Address saveAddress(Address address);

    void deleteAddressById(Long id);

    AddressDto convertToDto(Address address);

    Address convertToEntity(AddressDto addressDto);

    AddressDto updateOrCreateAddress(AddressDto addressDto);
}
