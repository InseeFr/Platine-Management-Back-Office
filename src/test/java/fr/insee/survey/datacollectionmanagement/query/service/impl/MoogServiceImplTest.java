package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MoogServiceImplTest {

    @Autowired
    MoogServiceImpl moogService;
    @Test
    public void createMoogAddressTest() throws Exception {
        Address address = new Address();
        address.setCityName("city");

        assertEquals("city", moogService.createAddressMoog(address));

        address.setZipCode("59000");
        assertEquals("59000 city", moogService.createAddressMoog(address));

        address.setCityName(null);
        assertEquals("59000", moogService.createAddressMoog(address));

        address.setCityName(" ");
        assertEquals("59000", moogService.createAddressMoog(address));

    }
}
