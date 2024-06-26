package fr.insee.survey.datacollectionmanagement.contact.service;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {

    public Address findById(Long id);

    public Page<Address> findAll(Pageable pageable);

    public Address saveAddress(Address address);

    public void deleteAddressById(Long id);

    public AddressDto convertToDto(Address address);

    public Address convertToEntity(AddressDto addressDto);

}
