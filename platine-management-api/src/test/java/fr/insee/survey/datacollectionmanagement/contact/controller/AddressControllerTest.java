package fr.insee.survey.datacollectionmanagement.contact.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AddressControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ContactService contactService;

    @Autowired
    AddressService addressService;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getAddressOk() throws Exception {
        String identifier = "CONT1";
        Contact contact = contactService.findByIdentifier(identifier);
        String json = createJsonAddress(contact);
        this.mockMvc.perform(get(Constants.API_CONTACTS_ID_ADDRESS, identifier)).andDo(print()).andExpect(status().isOk()).andExpect(content().json(json, false));
    }

    @Test
    void getAddressContacttNotFound() throws Exception {
        String identifier = "CONT500";
        this.mockMvc.perform(get(Constants.API_CONTACTS_ID_ADDRESS, identifier)).andDo(print()).andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void putAddressCreateUpdate() throws Exception {

        String identifier = "CONT5";
        Contact contact = contactService.findByIdentifier(identifier);
        Address addressBefore = contact.getAddress();

        // Before: delete existing address
        contact.setAddress(null);
        contact = contactService.saveContact(contact);
        addressService.deleteAddressById(addressBefore.getId());

        this.mockMvc.perform(get(Constants.API_CONTACTS_ID_ADDRESS, identifier)).andDo(print()).andExpect(status().is(HttpStatus.NOT_FOUND.value()));

        // Create address - status created
        Address addressCreated = initAddressMock(identifier);
        contact.setAddress(addressCreated);
        String jsonCreate = createJsonAddress(contact);
        this.mockMvc.perform(put(Constants.API_CONTACTS_ID_ADDRESS, identifier).content(jsonCreate).contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isCreated()).andExpect(content().json(jsonCreate.toString(), false));
        Contact contactAfterCreate = contactService.findByIdentifier(identifier);
        assertEquals(contactAfterCreate.getAddress().getCityName(), addressCreated.getCityName());
        assertEquals(contactAfterCreate.getAddress().getStreetName(), addressCreated.getStreetName());
        assertEquals(contactAfterCreate.getAddress().getCountryName(), addressCreated.getCountryName());

        // Update address - status OK
        Address addressUpdated = initAddressMock("UPDATE");
        contact.setAddress(addressUpdated);
        String jsonUpdate = createJsonAddress(contact);
        this.mockMvc.perform(put(Constants.API_CONTACTS_ID_ADDRESS, identifier).content(jsonUpdate).contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().json(jsonUpdate.toString(), false));
        Contact contactAfterUpdate = contactService.findByIdentifier(identifier);
        assertEquals(contactAfterUpdate.getAddress().getCityName(), addressUpdated.getCityName());
        assertEquals(contactAfterUpdate.getAddress().getStreetName(), addressUpdated.getStreetName());
        assertEquals(contactAfterUpdate.getAddress().getCountryName(), addressUpdated.getCountryName());


    }

    private Address initAddressMock(String identifier) {
        Address address = new Address();
        address.setCityName("cityName" + identifier);
        address.setStreetName("street name " + identifier);
        address.setStreetNumber(Integer.toString(identifier.length()));
        address.setCountryName("country " + identifier);
        return address;
    }

    private String createJsonAddress(Contact contact) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("cityName", contact.getAddress().getCityName());
        jo.put("streetName", contact.getAddress().getStreetName());
        jo.put("countryName", contact.getAddress().getCountryName());
        return jo.toString();
    }

}
