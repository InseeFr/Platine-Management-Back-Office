package fr.insee.survey.datacollectionmanagement.contact.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import fr.insee.survey.datacollectionmanagement.configuration.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.util.JsonUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
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
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
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

    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    QuestioningAccreditationService questioningAccreditationService;

    @Autowired
    QuestioningAccreditationRepository questioningAccreditationRepository;

    @RegisterExtension
    static WireMockExtension wmLdap = WireMockExtension.newInstance()
            .options(wireMockConfig().port(4444))
            .build();

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getContactOk() throws Exception {
        String identifier = "CONT1";
        Contact contact = contactService.findByIdentifier(identifier);
        String json = createJson(contact);
        this.mockMvc.perform(get(UrlConstants.API_CONTACTS_ID, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }

    @Test
    void getContactNotFound() throws Exception {
        String identifier = "CONT500";
        this.mockMvc.perform(get(UrlConstants.API_CONTACTS_ID, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void getContactsOk() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("totalElements", contactRepository.count());
        jo.put("numberOfElements", contactRepository.count());

        this.mockMvc.perform(get(UrlConstants.API_CONTACTS_ALL)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(jo.toString(), false));
    }

    @Test
    void putContactCreateUpdateDelete() throws Exception {
        String identifier = "TESTPUT";

        // create contact - status created
        Contact contact = initContact(identifier);
        String jsonContact = createJson(contact);
        mockMvc.perform(
                        put(UrlConstants.API_CONTACTS_ID, identifier).content(jsonContact).contentType(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(put(UrlConstants.API_CONTACTS_ID, identifier).content(jsonContactUpdate)
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
        mockMvc.perform(delete(UrlConstants.API_CONTACTS_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> contactService.findByIdentifier(identifier));
        assertTrue(contactEventService.findContactEventsByContact(contactFoundAfterUpdate).isEmpty());

        // delete contact not found
        mockMvc.perform(delete(UrlConstants.API_CONTACTS + identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void putContactAddressCreateUpdateDelete() throws Exception {
        String identifier = "TESTADDRESS";

        // create contact - status created
        Contact contact = initContactAddress(identifier);
        String jsonContact = createJsonContactAddress(contact);
        mockMvc.perform(
                        put(UrlConstants.API_CONTACTS_ID, identifier).content(jsonContact).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonContact.toString(), false));
        Contact countactFound = contactService.findByIdentifier(identifier);
        assertEquals(contact.getAddress().getCityName(), countactFound.getAddress().getCityName());

        // update contact - status ok
        String newCityName = "cityUpdate";
        contact.getAddress().setCityName(newCityName);
        String jsonContactUpdate = createJsonContactAddress(contact);
        mockMvc.perform(put(UrlConstants.API_CONTACTS_ID, identifier).content(jsonContactUpdate)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(jsonContactUpdate.toString(), false));
        Contact countactFoundAfterUpdate = contactService.findByIdentifier(identifier);
        assertEquals(contact.getAddress().getCityName(), countactFoundAfterUpdate.getAddress().getCityName());

        // delete contact
        mockMvc.perform(delete(UrlConstants.API_CONTACTS_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> contactService.findByIdentifier(identifier));

    }

    @Test
    void putContactsErrorId() throws Exception {
        String identifier = "NEWONE";
        String otherIdentifier = "WRONG";
        Contact contact = initContact(identifier);
        String jsonContact = createJson(contact);
        mockMvc.perform(put(UrlConstants.API_CONTACTS_ID, otherIdentifier).content(jsonContact)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(JsonUtil.createJsonErrorBadRequest("id and contact identifier don't match"), false));

    }

    @Test
    @DisplayName("Create contact and assign main accreditation")
    void putContactInterrogationInLdapAndAssignToInterrogationAsMain() throws Exception {

        UUID interrogationId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-000000000001");
        String email = "contact@insee.fr";
        String username = "TESTID";

        AddressDto address = new AddressDto();
        ContactDto contactDto = new ContactDto();

        contactDto.setEmail(email);
        contactDto.setAddress(address);

        String contactJson = new ObjectMapper().writeValueAsString(contactDto);
        String path = String.format("/v2/realms/%s/storages/%s/users",
                applicationConfig.getLdapApiRealm(),
                applicationConfig.getLdapApiStorage());

        wmLdap.stubFor(post(path)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createResponseBody(username))));

        this.mockMvc.perform(put(UrlConstants.API_NEW_MAIN_CONTACT_INTERROGATIONS_ASSIGN, interrogationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<Contact> createdContact = contactRepository.findById(username);
        assertThat(createdContact).isPresent();
        assertThat(createdContact.get().getEmail()).isEqualTo(email);
        assertThat(createdContact.get().getIdentifier()).isEqualTo(username);
        Optional<QuestioningAccreditation> optQuestioningAccreditation = questioningAccreditationRepository.findAccreditationsByQuestioningIdAndIsMainTrue(interrogationId);
        assertThat(optQuestioningAccreditation).isPresent();
        QuestioningAccreditation questioningAccreditation = optQuestioningAccreditation.get();
        assertThat(questioningAccreditation.isMain()).isTrue();
        assertThat(questioningAccreditation.getIdContact()).isEqualTo(username);
        assertThat(questioningAccreditation.getQuestioning().getId()).isEqualTo(interrogationId);
    }

    @Test
    @DisplayName("Should not Create contact and assign main accredition when Ldap API down")
    void putContactInterrogationInLdapAndAssignToInterrogationAsMainLdapApiDown() throws Exception {
        String email = "contact@insee.fr";
        String username = "TESTID";

        AddressDto address = new AddressDto();
        ContactDto contactDto = new ContactDto();

        contactDto.setEmail(email);
        contactDto.setAddress(address);

        String contactJson = new ObjectMapper().writeValueAsString(contactDto);
        String path = String.format("/v2/realms/%s/storages/%s/users",
                applicationConfig.getLdapApiRealm(),
                applicationConfig.getLdapApiStorage());

        wmLdap.stubFor(post(path)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        this.mockMvc.perform(put(UrlConstants.API_NEW_MAIN_CONTACT_INTERROGATIONS_ASSIGN, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        Optional<Contact> createdContact = contactRepository.findById(username);
        assertThat(createdContact).isNotPresent();
        List<QuestioningAccreditation> questioningAccreditations = questioningAccreditationService.findByContactIdentifier(username);
        assertThat(questioningAccreditations).isEmpty();
    }

    String createResponseBody(String username)
    {
        return String.format("""
        {
            "username": "%s",
            "habilitations": [
                {
                    "id": "%s",
                    "application": "%s",
                    "role": "%s",
                    "property": "%s"
                }
            ]
        }
        """,
                username,
                applicationConfig.getLdapApiId(), applicationConfig.getLdapApiApplication(), applicationConfig.getLdapApiRole(), applicationConfig.getLdapApiProperty()
        );
    }

    private Contact initContact(String identifier) {
        Contact contactMock = new Contact();
        contactMock.setIdentifier(identifier);
        contactMock.setEmail("test@insee.fr");
        contactMock.setFirstName("firstName" + identifier);
        contactMock.setLastName("lastName" + identifier);
        contactMock.setGender(GenderEnum.MALE);

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

    @Test
    void getContactInfoOk() throws Exception {
        String contactId = "CONT1";

        this.mockMvc.perform(get(UrlConstants.API_CONTACT)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser(contactId, AuthorityRoleEnum.RESPONDENT))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getContactInfoNotFound() throws Exception {
        String contactId = "DOES_NOT_EXIST";

        this.mockMvc.perform(get(UrlConstants.API_CONTACT)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser(contactId, AuthorityRoleEnum.RESPONDENT))))
                .andExpect(status().isNotFound());
    }

    @Test
    void putContactInfoOk() throws Exception {
        String contactId = "CONT1";

        JSONObject joPayload = new JSONObject();
        joPayload.put("identifier", contactId);
        joPayload.put("lastName", "Doe");
        joPayload.put("firstName", "John");
        joPayload.put("civility", "Undefined");
        joPayload.put("email", "john.doe@example.com");

        this.mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .put(UrlConstants.API_CONTACT)
                                .with(authentication(AuthenticationUserProvider.getAuthenticatedUser(contactId, AuthorityRoleEnum.RESPONDENT)))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(joPayload.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void putContactInfo_NotMatchingIdentifiers_ShouldReturn403() throws Exception {
        String contactId = "CONT1";
        String payloadId = "OTHER";

        JSONObject joPayload = new JSONObject();
        joPayload.put("identifier", payloadId);
        joPayload.put("lastName", "Doe");
        joPayload.put("firstName", "John");
        joPayload.put("civility", "Undefined");
        joPayload.put("email", "john.doe@example.com");

        this.mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .put(UrlConstants.API_CONTACT)
                                .with(authentication(AuthenticationUserProvider.getAuthenticatedUser(contactId, AuthorityRoleEnum.RESPONDENT)))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(joPayload.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
