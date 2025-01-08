package fr.insee.survey.datacollectionmanagement.contact.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.util.JsonUtil;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ContactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ContactService contactService;

    @Autowired
    ContactEventService contactEventService;

    @Autowired
    ContactRepository contactRepository;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }


    @Test
    void getContactOk() throws Exception {
        String identifier = "CONT1";
        Contact contact = contactService.findByIdentifier(identifier);
        String json = createJson(contact);
        this.mockMvc.perform(get(Constants.API_CONTACTS_ID, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }

    @Test
    void getContactNotFound() throws Exception {
        String identifier = "CONT500";
        this.mockMvc.perform(get(Constants.API_CONTACTS_ID, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void getContactsOk() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("totalElements", contactRepository.count());
        jo.put("numberOfElements", contactRepository.count());

        this.mockMvc.perform(get(Constants.API_CONTACTS_ALL)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(jo.toString(), false));
    }

    @Test
    void putContactCreateUpdateDelete() throws Exception {
        String identifier = "TESTPUT";

        // create contact - status created
        Contact contact = initContact(identifier);
        String jsonContact = createJson(contact);
        mockMvc.perform(
                        put(Constants.API_CONTACTS_ID, identifier).content(jsonContact).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonContact.toString(), false));
        Contact contactFound = contactService.findByIdentifier(identifier);
        assertEquals(contact.getLastName(), contactFound.getLastName());
        assertEquals(contact.getFirstName(), contactFound.getFirstName());
        assertEquals(contact.getEmail(), contactFound.getEmail());
        List<ContactEvent> list = new ArrayList<>(contactEventService.findContactEventsByContact(contactFound));
        assertEquals(1, list.size());
        assertEquals(ContactEventTypeEnum.create, list.get(0).getType());

        // update contact - status ok
        contact.setLastName("lastNameUpdate");
        String jsonContactUpdate = createJson(contact);
        mockMvc.perform(put(Constants.API_CONTACTS_ID, identifier).content(jsonContactUpdate)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(jsonContactUpdate.toString(), false));
        Contact contactFoundAfterUpdate = contactService.findByIdentifier(identifier);
        assertEquals("lastNameUpdate", contactFoundAfterUpdate.getLastName());
        assertEquals(contact.getFirstName(), contactFoundAfterUpdate.getFirstName());
        assertEquals(contact.getEmail(), contactFoundAfterUpdate.getEmail());
        List<ContactEvent> listUpdate = new ArrayList<>(
                contactEventService.findContactEventsByContact(contactFoundAfterUpdate));
        assertEquals(2, listUpdate.size());
        assertEquals(ContactEventTypeEnum.update, listUpdate.get(1).getType());

        // delete contact
        mockMvc.perform(delete(Constants.API_CONTACTS_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> contactService.findByIdentifier(identifier));
        assertTrue(contactEventService.findContactEventsByContact(contactFoundAfterUpdate).isEmpty());

        // delete contact not found
        mockMvc.perform(delete(Constants.API_CONTACTS + identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void putContactAddressCreateUpdateDelete() throws Exception {
        String identifier = "TESTADDRESS";

        // create contact - status created
        Contact contact = initContactAddress(identifier);
        String jsonContact = createJsonContactAddress(contact);
        mockMvc.perform(
                        put(Constants.API_CONTACTS_ID, identifier).content(jsonContact).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonContact.toString(), false));
        Contact countactFound = contactService.findByIdentifier(identifier);
        assertEquals(contact.getAddress().getCityName(), countactFound.getAddress().getCityName());

        // update contact - status ok
        String newCityName = "cityUpdate";
        contact.getAddress().setCityName(newCityName);
        String jsonContactUpdate = createJsonContactAddress(contact);
        mockMvc.perform(put(Constants.API_CONTACTS_ID, identifier).content(jsonContactUpdate)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(jsonContactUpdate.toString(), false));
        Contact countactFoundAfterUpdate = contactService.findByIdentifier(identifier);
        assertEquals(contact.getAddress().getCityName(), countactFoundAfterUpdate.getAddress().getCityName());

        // delete contact
        mockMvc.perform(delete(Constants.API_CONTACTS_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> contactService.findByIdentifier(identifier));

    }

    @Test
    void putContactsErrorId() throws Exception {
        String identifier = "NEWONE";
        String otherIdentifier = "WRONG";
        Contact contact = initContact(identifier);
        String jsonContact = createJson(contact);
        mockMvc.perform(put(Constants.API_CONTACTS_ID, otherIdentifier).content(jsonContact)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(JsonUtil.createJsonErrorBadRequest("id and contact identifier don't match"), false));

    }

    private Contact initContact(String identifier) {
        Contact contactMock = new Contact();
        contactMock.setIdentifier(identifier);
        contactMock.setEmail("test@insee.fr");
        contactMock.setFirstName("firstName" + identifier);
        contactMock.setLastName("lastName" + identifier);
        contactMock.setGender(GenderEnum.Male);

        return contactMock;
    }

    private Contact initContactAddress(String identifier) {
        Contact contact = initContact(identifier);
        Address address = initAddress(identifier);
        contact.setAddress(address);
        return contact;
    }

    private Address initAddress(String identifier) {
        Address address = new Address();
        address.setCityName("city " + identifier);
        address.setCountryName("country " + identifier);
        address.setStreetName("steet " + identifier);
        address.setStreetNumber(identifier);
        return address;
    }

    private String createJson(Contact contact) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("identifier", contact.getIdentifier());
        jo.put("lastName", contact.getLastName());
        jo.put("firstName", contact.getFirstName());
        jo.put("email", contact.getEmail());
        jo.put("civility", contact.getGender());
        return jo.toString();
    }

    private String createJsonContactAddress(Contact contact) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("identifier", contact.getIdentifier());
        jo.put("lastName", contact.getLastName());
        jo.put("firstName", contact.getFirstName());
        jo.put("email", contact.getEmail());
        jo.put("civility", contact.getGender());
        jo.put("address", createJsonAddress(contact));
        return jo.toString();

    }

    private JSONObject createJsonAddress(Contact contact) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("cityName", contact.getAddress().getCityName());
        jo.put("streetName", contact.getAddress().getStreetName());
        jo.put("countryName", contact.getAddress().getCountryName());
        return jo;
    }

}
