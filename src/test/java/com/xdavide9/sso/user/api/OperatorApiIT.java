package com.xdavide9.sso.user.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.util.JsonParserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tests the integration between OperatorController, OperatorService and UserRepository
// against a db with 1 admin, 1 operator, 1 plain user; refer to TestingDatabaseConfig.java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OperatorApiIT {

    @Autowired
    private MockMvc mockMvc;
    // using parser to help write json even if theoretically only mockMvc should be used
    // in integration testing
    @Autowired
    private JsonParserService parser;

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldGetUserByUsername(String loginUsername, String loginPassword) throws Exception {
        // given
        // login to get jwt token first
        LoginRequest loginRequest = new LoginRequest(loginUsername, loginPassword);
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(loginRequest))
        );
        String loginJsonResponse = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = parser.java(loginJsonResponse, AuthenticationResponse.class);
        String username = "userUsername";
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", format("Bearer %s", authenticationResponse.token()))
        );
        // then
        resultActions.andExpect(status().isOk());
        String json = resultActions.andReturn().getResponse().getContentAsString();
        User user = parser.java(json, User.class);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo("user@email.com");
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldNotGetUserByUsernameUserDoesNotExist(String loginUsername, String loginPassword) throws Exception {
        // given
        // login to get jwt token first
        LoginRequest loginRequest = new LoginRequest(loginUsername, loginPassword);
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(loginRequest))
        );
        String loginJsonResponse = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = parser.java(loginJsonResponse, AuthenticationResponse.class);
        String username = "nonExistent";
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", format("Bearer %s", authenticationResponse.token()))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String json = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(json, new TypeReference<>() {});
        assertThat(responseBody.get("error")).isEqualTo("Cannot get information about user");
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(responseBody.get("message")).isEqualTo(format("User with username [%s] not found.", username));
    }

    @Test
    void itShouldNotGetUserByUsernameTokenIsMissing() throws Exception {
        // given
        String username = "userUsername";
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
    }

    @Test
    void itShouldNotGetUserByUsernameNotEnoughAuthorization() throws Exception {
        // given
        String userUsername = "userUsername";
        String userPassword = "userPassword";
        LoginRequest loginRequest = new LoginRequest(userUsername, userPassword);
        String jsonBody = parser.json(loginRequest);
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
        );
        loginResultActions.andExpect(status().isOk());
        AuthenticationResponse response = parser.java(
                loginResultActions.andReturn().getResponse().getContentAsString(),
                AuthenticationResponse.class
        );
        String token = response.token();
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users/username/userUsername")
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        // see SecurityConfig AccessDeniedHandler
        assertThat(responseBody).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }
    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldGetUserByUuid(String loginUsername, String loginPassword) throws Exception {
        // given
        // login for authorization
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(loginUsername, loginPassword)))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        // now get the user by username (already tested)
        ResultActions getByUsernameResultActions = mockMvc.perform(
                get("/api/v0.0.1/users/username/userUsername")
                        .header("Authorization", format("Bearer %s", token))
        );
        getByUsernameResultActions.andExpect(status().isOk());
        String getByUsernameResponseBody = getByUsernameResultActions.andReturn().getResponse().getContentAsString();
        User userByUsername = parser.java(getByUsernameResponseBody, User.class);
        UUID uuid = userByUsername.getUuid();
        // when
        // now the actual getByUuid
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/uuid/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        User user = parser.java(responseBody, User.class);
        assertThat(user).isEqualTo(userByUsername);
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldNotGetUserByUuidUserDoesNotExist(String loginUsername, String loginPassword) throws Exception {
        // given
        // login for authorization
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(loginUsername, loginPassword)))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        UUID uuid = UUID.randomUUID();
        // when
        // now the actual getByUuid
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/uuid/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String json = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(json, new TypeReference<>() {});
        assertThat(responseBody.get("error")).isEqualTo("Cannot get information about user");
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(responseBody.get("message")).isEqualTo(format("User with uuid [%s] not found.", uuid));
    }

    @Test
    void itShouldNotGetByUserUuidTokenIsMissing() throws Exception {
        // given
        // does not matter it does not match a record in the database as it should be denied
        UUID uuid = UUID.randomUUID();
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/uuid/%s", uuid))
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
    }

    @Test
    void itShouldNotGetUserByUuidNotEnoughAuthorization() throws Exception {
        // given
        // does not matter it does not match a record in the database as it should be denied
        UUID uuid = UUID.randomUUID();
        // login for weak token
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest("userUsername", "userPassword")))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        // does not matter it does
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/uuid/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldGetUserByEmail(String loginUsername, String loginPassword) throws Exception {
        // given
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(loginUsername, loginPassword)))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users/email/user@email.com")
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        User user = parser.java(responseBody, User.class);
        assertThat(user.getUsername()).isEqualTo("userUsername");
        assertThat(user.getEmail()).isEqualTo("user@email.com");
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldNotGetUserByEmailUserDoesNotExist(String loginUsername, String loginPassword) throws Exception {
        // given
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(loginUsername, loginPassword)))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        String email = "wrong@email.com";
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/email/%s", email))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String json = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(json, new TypeReference<>() {});
        assertThat(responseBody.get("error")).isEqualTo("Cannot get information about user");
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(responseBody.get("message")).isEqualTo(format("User with email [%s] not found.", email));
    }

    @Test
    void itShouldNotGetUserByEmailTokenIsMissing() throws Exception {
        // given
        String email = "user@email.com";
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/email/%s", email))
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
    }

    @Test
    void itShouldNotGetUserByEmailNotEnoughAuthorization() throws Exception {
        // given
        String loginUsername = "userUsername";
        String loginPassword = "userPassword";
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(loginUsername, loginPassword)))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users/email/userEmail")
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    // HELPER METHODS

    private User getUser(String username, String token) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users/username/" + username)
                        .header("Authorization", format("Bearer %s", token))
        );
        resultActions.andExpect(status().isOk());
        return parser.java(resultActions.andReturn().getResponse().getContentAsString(), User.class);
    }

    private User getUserWithRoleUser(String token) throws Exception {
        return getUser("userUsername", token);
    }
    private User getUserWithRoleOperator(String token) throws Exception {
        return getUser("operatorUsername", token);
    }
    private User getUserWithRoleAdmin(String token) throws Exception {
        return getUser("adminUsername", token);
    }

    private String login(String username, String password) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(username, password)))
        );
        resultActions.andExpect(status().isOk());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = parser.java(response, AuthenticationResponse.class);
        return authenticationResponse.token();
    }

    private String loginAsOperator() throws Exception {
        return login("operatorUsername", "operatorPassword");
    }

    private String loginAsAdmin() throws Exception {
        return login("adminUsername", "adminPassword");
    }

    private String loginAsUser() throws Exception {
        return login("userUsername", "userPassword");
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    @Transactional
    void itShouldTimeOutUserCorrectlyWithSpecifiedDuration(String loginUsername, String loginPassword) throws Exception {
        // given
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(loginUsername, loginPassword)))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        UUID uuid = getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/timeout/%s?duration=1000", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo(format("User with uuid [%s] has been timed out for [1000] milliseconds", uuid));
        User timedOutUser = getUserWithRoleUser(token);
        assertThat(timedOutUser.isEnabled()).isFalse();
        Thread.sleep(1000);
        User enabledUser = getUserWithRoleUser(token);
        assertThat(enabledUser.isEnabled()).isTrue();
    }

    // test with the default duration is in TimeOutDefaultDurationIT.java

    @Test
    void itShouldNotTimeOutUserTokenIsMissing() throws Exception {
        // given
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/timeout/%s?duration=2000", UUID.randomUUID()))
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
    }

    @Test
    void itShouldNotTimeOutUserNotEnoughAuthorization() throws Exception {
        // given
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest("userUsername", "userPassword")))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/timeout/%s?duration=2000", UUID.randomUUID()))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @Test
    void itShouldNotTimeOutUserDoesNotExist() throws Exception {
        // given
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest("operatorUsername", "operatorPassword")))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        UUID uuid = UUID.randomUUID();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/timeout/%s?duration=2000", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(response, new TypeReference<>(){});
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(responseBody.get("error")).isEqualTo("Cannot time out user");
        assertThat(responseBody.get("message")).isEqualTo(format("User with uuid [%s] not found.", uuid));
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    @Transactional
    void itShouldChangeUsernameCorrectly(String loginUsername, String loginPassword) throws Exception {
        // given
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(loginUsername, loginPassword)))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        UUID uuid = getUserWithRoleUser(token).getUuid();
        String username = "username";
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/username/%s?username=%s", uuid, username))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo(format("Username of user with uuid [%s] has been changed correctly to [%s]", uuid, username));
    }

    @Test
    void itShouldNotChangeUsernameTokenIsMissing() throws Exception {
        // given
        String token = loginAsOperator();
        UUID uuid = getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/username/%s?username=any", uuid))
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
    }

    @Test
    void itShouldNotChangeUsernameNotEnoughAuthorization() throws Exception {
        // given
        String strongToken = loginAsOperator();
        String weakToken = loginAsUser();
        UUID uuid = getUserWithRoleUser(strongToken).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/username/%s?username=any", uuid))
                        .header("Authorization", format("Bearer %s", weakToken))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @Test
    void itShouldNotChangeUsernameUserNotFound() throws Exception {
        // given
        String token = loginAsOperator();
        UUID uuid = UUID.randomUUID();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/username/%s?username=any", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(response, new TypeReference<>() {});
        assertThat(responseBody.get("error")).isEqualTo("Cannot get information about user");
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(responseBody.get("message")).isEqualTo(format("User with uuid [%s] not found.", uuid));
    }

    @Test
    void itShouldNotChangeUsernameBecauseItIsTaken() throws Exception {
        // given
        String token = loginAsOperator();
        UUID uuid = getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/username/%s?username=operatorUsername", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isConflict());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(response, new TypeReference<>() {});
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
        assertThat(responseBody.get("error")).isEqualTo("Username already taken");
        assertThat(responseBody.get("message")).isEqualTo(format("Cannot change username of user with uuid [%s]", uuid));
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    @Transactional
    void itShouldChangeEmailCorrectly(String loginUsername, String loginPassword) throws Exception {
        // given
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest(loginUsername, loginPassword)))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String token = loginResponse.token();
        UUID uuid = getUserWithRoleUser(token).getUuid();
        String email = "email@email.com";
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/email/%s?email=%s", uuid, email))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo(format("Email of user with uuid [%s] has been changed correctly to [%s]", uuid, email));
    }

    @Test
    void itShouldNotChangeEmailTokenIsMissing() throws Exception {
        // given
        String token = loginAsOperator();
        UUID uuid = getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/email/%s?email=any", uuid))
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
    }

    @Test
    void itShouldNotChangeEmailNotEnoughAuthorization() throws Exception {
        // given
        String strongToken = loginAsOperator();
        String weakToken = loginAsUser();
        UUID uuid = getUserWithRoleUser(strongToken).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/email/%s?email=any", uuid))
                        .header("Authorization", format("Bearer %s", weakToken))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @Test
    void itShouldNotChangeEmailUserNotFound() throws Exception {
        // given
        String token = loginAsOperator();
        UUID uuid = UUID.randomUUID();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/email/%s?email=any", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(response, new TypeReference<>() {});
        assertThat(responseBody.get("error")).isEqualTo("Cannot get information about user");
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(responseBody.get("message")).isEqualTo(format("User with uuid [%s] not found.", uuid));
    }

    @Test
    void itShouldNotChangeEmailBecauseItIsTaken() throws Exception {
        // given
        String token = loginAsOperator();
        UUID uuid = getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/email/%s?email=operatorUsername", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isConflict());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(response, new TypeReference<>() {});
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
        assertThat(responseBody.get("error")).isEqualTo("Username already taken");
        assertThat(responseBody.get("message")).isEqualTo(format("Cannot change username of user with uuid [%s]", uuid));
    }
}
