package fr.insee.survey.datacollectionmanagement.contact.service;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {

    Address findById(Long id);

    Page<Address> findAll(Pageable pageable);

    Address saveAddress(Address address);

    void deleteAddressById(Long id);

    AddressDto convertToDto(Address address);

    Address convertToEntity(AddressDto addressDto);

}
