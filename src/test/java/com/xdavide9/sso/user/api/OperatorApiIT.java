package com.xdavide9.sso.user.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xdavide9.sso.authentication.AuthenticationResponse;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.util.JsonParserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
