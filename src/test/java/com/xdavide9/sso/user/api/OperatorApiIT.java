package com.xdavide9.sso.user.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xdavide9.sso.common.util.JsonParserService;
import com.xdavide9.sso.common.util.TestAuthenticator;
import com.xdavide9.sso.user.User;
import com.xdavide9.sso.user.change.UserChange;
import com.xdavide9.sso.user.fields.UserField;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tests the integration between OperatorController, OperatorService and UserRepository
// against a db with 1 admin, 1 operator, 1 plain user; refer to TestingDatabaseConfig.java

// Uses TestAuthenticator

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestAuthenticator.class)
public class OperatorApiIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonParserService parser;
    @Autowired
    private TestAuthenticator authenticator;

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    void itShouldGetAllUsers(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void itShouldNotGetAllUsersTokenIsMissing() throws Exception {
        // given
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users")
        );
        // then
        resultActions.andExpect(status().isUnauthorized());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("Missing jwt Token. Every request should include a valid jwt token to authenticate to the server.");
    }

    @Test
    void itShouldNotGetAllUsersNotEnoughAuthorization() throws Exception {
        // given
        String token = authenticator.login("userUsername", "userPassword");
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users")
                        .contentType(MediaType.APPLICATION_JSON)
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
    void itShouldGetUserByUsername(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        String username = "userUsername";
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String json = resultActions.andReturn().getResponse().getContentAsString();
        User user = parser.java(json, User.class);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo("user@email.com");
    }

    @Test
    void itShouldNotGetUserByUsernameUserDoesNotExist() throws Exception {
        // given
        String token = authenticator.loginAsOperator();
        String username = "nonExistent";
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", format("Bearer %s", token))
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
        String token = authenticator.loginAsUser();
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
        String token = authenticator.login(loginUsername, loginPassword);
        User user = authenticator.getUserWithRoleUser(token);
        UUID uuid = user.getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/uuid/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        User returnedUser = parser.java(responseBody, User.class);
        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    void itShouldNotGetUserByUuidUserDoesNotExist() throws Exception {
        // given
        String token = authenticator.loginAsOperator();
        UUID uuid = UUID.randomUUID();
        // when
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
        UUID uuid = UUID.randomUUID();
        String token = authenticator.loginAsUser();
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
        String token = authenticator.login(loginUsername, loginPassword);
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

    @Test
    void itShouldNotGetUserByEmailUserDoesNotExist() throws Exception {
        // given
        String token = authenticator.loginAsOperator();
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
        String token = authenticator.loginAsUser();
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

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    @Transactional
    void itShouldTimeOutUserCorrectlyWithSpecifiedDuration(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        User user = authenticator.getUserWithRoleUser(token);
        UUID uuid = user.getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/timeout/%s?duration=1000", user.getUuid()))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo(format("User with uuid [%s] has been timed out for [1000] minutes.", uuid));
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
        String weakToken = authenticator.loginAsUser();
        String strongToken = authenticator.loginAsOperator();
        UUID uuid = authenticator.getUserWithRoleUser(strongToken).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/timeout/%s?duration=2000", uuid))
                        .header("Authorization", format("Bearer %s", weakToken))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @Test
    void itShouldNotTimeOutUserDoesNotExist() throws Exception {
        // given
        String token = authenticator.loginAsOperator();
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
        String token = authenticator.login(loginUsername, loginPassword);
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
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
        // change should be recorded
        ResultActions getChanges = mockMvc.perform(
                get("/api/v0.0.1/users/changes")
                        .header("Authorization", format("Bearer %s", token))
        );
        String json = getChanges.andReturn().getResponse().getContentAsString();
        List<UserChange> changes = parser.java(json, new TypeReference<>(){});
        UserChange userChange = changes.get(0);
        assertThat(userChange.getField()).isEqualTo(UserField.USERNAME);
        assertThat(userChange.getPreviousValue()).isEqualTo("userUsername");
        assertThat(userChange.getUpdatedValue()).isEqualTo("username");
    }

    @Test
    void itShouldNotChangeUsernameTokenIsMissing() throws Exception {
        // given
        String token = authenticator.loginAsOperator();
        UUID uuid = authenticator.getUserWithRoleOperator(token).getUuid();
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
        String strongToken = authenticator.loginAsOperator();
        String weakToken = authenticator.loginAsUser();
        UUID uuid = authenticator.getUserWithRoleUser(strongToken).getUuid();
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
        String token = authenticator.loginAsOperator();
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
        String token = authenticator.loginAsOperator();
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
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
        assertThat(responseBody.get("message")).isEqualTo(format("Username [operatorUsername] is already taken", uuid));
    }

    @Test
    void itShouldNotChangeUsernameInvalidInput() throws Exception {
        // given
        String token = authenticator.loginAsOperator();
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/username/%s?username=", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isBadRequest());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(response, new TypeReference<>() {});
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
        assertThat(responseBody.get("error")).isEqualTo("Invalid username");
        assertThat(responseBody.get("message")).isEqualTo("Username [] is not valid, provide a new one");
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "adminUsername,adminPassword"
    })
    @Transactional
    void itShouldChangeEmailCorrectly(String loginUsername, String loginPassword) throws Exception {
        // given
        String token = authenticator.login(loginUsername, loginPassword);
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
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
        // change should be recorded
        ResultActions getChanges = mockMvc.perform(
                get("/api/v0.0.1/users/changes")
                        .header("Authorization", format("Bearer %s", token))
        );
        String json = getChanges.andReturn().getResponse().getContentAsString();
        List<UserChange> changes = parser.java(json, new TypeReference<>(){});
        UserChange userChange = changes.get(0);
        assertThat(userChange.getField()).isEqualTo(UserField.EMAIL);
        assertThat(userChange.getPreviousValue()).isEqualTo("user@email.com");
        assertThat(userChange.getUpdatedValue()).isEqualTo("email@email.com");
    }

    @Test
    void itShouldNotChangeEmailTokenIsMissing() throws Exception {
        // given
        String token = authenticator.loginAsOperator();
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
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
        String strongToken = authenticator.loginAsOperator();
        String weakToken = authenticator.loginAsUser();
        UUID uuid = authenticator.getUserWithRoleUser(strongToken).getUuid();
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
        String token = authenticator.loginAsOperator();
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
        String token = authenticator.loginAsOperator();
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/email/%s?email=operator@email.com", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isConflict());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(response, new TypeReference<>() {});
        assertThat(responseBody.get("status")).isEqualTo(CONFLICT.toString());
        assertThat(responseBody.get("error")).isEqualTo("Email already taken");
        assertThat(responseBody.get("message")).isEqualTo("Email [operator@email.com] is already taken");
    }

    @Test
    void itShouldNotChangeEmailInvalidUserInput() throws Exception {
        // given
        String token = authenticator.loginAsOperator();
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/change/email/%s?email=email", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isBadRequest());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(response, new TypeReference<>() {});
        assertThat(responseBody.get("status")).isEqualTo(BAD_REQUEST.toString());
        assertThat(responseBody.get("error")).isEqualTo("Invalid email");
        assertThat(responseBody.get("message")).isEqualTo("Email [email] is not valid, provide a new one");
    }
}
