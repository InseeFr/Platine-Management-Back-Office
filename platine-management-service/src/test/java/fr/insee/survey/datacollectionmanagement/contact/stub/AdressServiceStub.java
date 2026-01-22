package fr.insee.survey.datacollectionmanagement.contact.stub;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;

@RequiredArgsConstructor
public class AdressServiceStub implements AddressService {
    ArrayList<Address> addresses = new ArrayList<>();

    private final ModelMapper mapper;

    @Override
    public Address saveAddress(Address address) {
        addresses.add(address);
        return address;
    }

    @Override
    public void deleteAddressById(Long id) {
        addresses.removeIf(a -> a.getId().equals(id));
    }

    @Override
    public AddressDto convertToDto(Address address) {
        return mapper.map(address, AddressDto.class);
    }

    @Override
    public Address convertToEntity(AddressDto addressDto) {
        return mapper.map(addressDto, Address.class);
    }

    @Override
    public AddressDto updateOrCreateAddress(AddressDto addressDto) {
        deleteAddressById(addressDto.getId());
        saveAddress(convertToEntity(addressDto));
        return addressDto;
    }
}
