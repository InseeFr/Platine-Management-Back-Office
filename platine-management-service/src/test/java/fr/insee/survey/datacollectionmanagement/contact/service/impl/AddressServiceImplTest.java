package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddressServiceImplTest {

    private final AddressRepository addressRepository = mock(AddressRepository.class);
    private final ModelMapper modelMapper = new ModelMapper();

    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        addressService = new AddressServiceImpl(addressRepository, modelMapper);
    }

    @Test
    void testUpdateOrCreateAddress_shouldSaveAndReturnDto() {
        // GIVEN
        AddressDto inputDto = new AddressDto();
        inputDto.setStreetNumber("12");
        inputDto.setStreetType("rue");
        inputDto.setStreetName("des Lilas");
        inputDto.setCityName("Lyon");
        inputDto.setZipCode("69008");

        Address savedEntity = new Address();
        savedEntity.setId(42L);
        savedEntity.setStreetNumber("12");
        savedEntity.setStreetType("rue");
        savedEntity.setStreetName("des Lilas");
        savedEntity.setCityName("Lyon");
        savedEntity.setZipCode("69008");

        when(addressRepository.save(any(Address.class))).thenReturn(savedEntity);

        // WHEN
        AddressDto result = addressService.updateOrCreateAddress(inputDto);

        // THEN
        assertEquals("12", result.getStreetNumber());
        assertEquals("rue", result.getStreetType());
        assertEquals("des Lilas", result.getStreetName());
        assertEquals("Lyon", result.getCityName());
        assertEquals("69008", result.getZipCode());
    }
}
