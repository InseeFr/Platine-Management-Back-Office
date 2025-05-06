package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.repository.AddressRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    private final ModelMapper modelMapper;

    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Address %s not found", id)));
    }

    @Override
    public Page<Address> findAll(Pageable pageable) {
        return addressRepository.findAll(pageable);
    }

    @Override
    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddressById(Long id) {
        addressRepository.deleteById(id);

    }

    public AddressDto convertToDto(Address address) {
        return modelMapper.map(address, AddressDto.class);
    }

    public Address convertToEntity(AddressDto addressDto) {
        return modelMapper.map(addressDto, Address.class);
    }

    @Override
    public AddressDto updateOrCreateAddress(AddressDto addressDto) {
        Address address = modelMapper.map(addressDto, Address.class);
        address = addressRepository.save(address);
        return  modelMapper.map(address, AddressDto.class);
    }

}
