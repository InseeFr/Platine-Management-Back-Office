package fr.insee.survey.datacollectionmanagement.user.controller;

import fr.insee.survey.datacollectionmanagement.config.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.domain.UserEvent;
import fr.insee.survey.datacollectionmanagement.user.domain.UserEvent.UserEventType;
import fr.insee.survey.datacollectionmanagement.user.repository.UserRepository;
import fr.insee.survey.datacollectionmanagement.user.service.UserEventService;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
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
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    UserEventService userEventService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }


    @Test
    void getUserNotFound() throws Exception {
        String identifier = "CONT500";
        this.mockMvc.perform(get(Constants.API_USERS_ID, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void getUserOk() throws Exception {
        String identifier = "USER1";
        this.mockMvc.perform(get(Constants.API_USERS_ID, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));

    }

    @Test
    void getUsersOk() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("totalElements", userRepository.count());
        jo.put("numberOfElements", userRepository.count());

        this.mockMvc.perform(get(Constants.API_USERS_ALL)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(jo.toString(), false));
    }

    @Test
    void putUserCreateUpdateDelete() throws Exception {
        String identifier = "TESTPUT";

        // create user - status created
        User user = initGestionnaire(identifier);
        String jsonUser = createJson(user);
        mockMvc.perform(
                        put(Constants.API_USERS_ID, identifier).content(jsonUser).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonUser.toString(), false));
        assertDoesNotThrow(() -> userService.findByIdentifier(identifier));
        User userFound = userService.findByIdentifier(identifier);
        assertEquals(user.getIdentifier(), userFound.getIdentifier());
        assertEquals(user.getRole(), userFound.getRole());

        // update user - status ok
        user.setRole(User.UserRoleType.ASSISTANCE);
        String jsonUserUpdate = createJson(user);
        mockMvc.perform(put(Constants.API_USERS_ID, identifier).content(jsonUserUpdate)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(jsonUserUpdate.toString(), false));
        User userFoundAfterUpdate = userService.findByIdentifier(identifier);
        assertEquals(User.UserRoleType.ASSISTANCE, userFoundAfterUpdate.getRole());
        List<UserEvent> listUpdate = new ArrayList<>(
                userEventService.findUserEventsByUser(userFoundAfterUpdate));
        assertEquals(2, listUpdate.size());
        assertEquals(UserEventType.UPDATE, listUpdate.get(1).getType());

        // delete user
        mockMvc.perform(delete(Constants.API_USERS_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> userService.findByIdentifier(identifier));

        assertTrue(userEventService.findUserEventsByUser(userFoundAfterUpdate).isEmpty());

        // delete user not found
        mockMvc.perform(delete(Constants.API_USERS_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void putUsersErrorId() throws Exception {
        String identifier = "NEWONE";
        String otherIdentifier = "WRONG";
        User user = initGestionnaire(identifier);
        String jsonUser = createJson(user);
        mockMvc.perform(put(Constants.API_USERS_ID, otherIdentifier).content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(JsonUtil.createJsonErrorBadRequest("id and user identifier don't match")));

    }

    private User initGestionnaire(String identifier) {
        return initUser(identifier, User.UserRoleType.GESTIONNAIRE);
    }

    private User initUser(String identifier, User.UserRoleType role) {
        User userMock = new User();
        userMock.setIdentifier(identifier);
        userMock.setRole(role);
        return userMock;
    }


    private String createJson(User user) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("identifier", user.getIdentifier());
        jo.put("role", user.getRole().name());
        return jo.toString();
    }
}
