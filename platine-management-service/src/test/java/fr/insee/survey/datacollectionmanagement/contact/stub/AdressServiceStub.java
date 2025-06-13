package fr.insee.survey.datacollectionmanagement.contact.stub;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
public class AdressServiceStub implements AddressService {
    ArrayList<Address> addresses = new ArrayList<>();

    private final ModelMapper mapper;

    @Override
    public Address findById(Long id) {
        Optional<Address> address = addresses.stream().filter(a -> a.getId().equals(id)).findFirst();
        return address.orElse(null);
    }

    @Override
    public Page<Address> findAll(Pageable pageable) {
        return null;
    }

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
