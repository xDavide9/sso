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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tests the integration between AdminController, AdminService and UserRepository

// All tests should be wrapped in transactions because they modify database state
// and might lead to test failure elsewhere

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminApiIT {
    @Autowired
    private MockMvc mockMvc;
    // using parser to help write json even if theoretically only mockMvc should be used
    // in integration testing
    @Autowired
    private JsonParserService parser;

    // HELPER METHODS (functionality widely tested before)

    private String login(String loginUsername, String loginPassword) throws Exception {
        LoginRequest loginRequest = new LoginRequest(loginUsername, loginPassword);
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(loginRequest))
        );
        loginResultActions.andExpect(status().isOk());
        String responseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(responseBody, AuthenticationResponse.class);
        return loginResponse.token();
    }

    private String loginAsAdmin() throws Exception {
        return login("adminUsername", "adminPassword");
    }

    private String loginAsOperator() throws Exception {
        return login("operatorUsername", "operatorPassword");
    }

    private String loginAsUser() throws Exception {
        return login("userUsername", "userPassword");
    }

    private UUID getUuid(String token, String username) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
                        .header("Authorization", format("Bearer %s", token))
        );
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        User user = parser.java(responseBody, User.class);
        return user.getUuid();
    }

    private UUID getUuidOfUser(String token) throws Exception {
        return getUuid(token, "userUsername");
    }

    private UUID getUuidOfOperator(String token) throws Exception {
        return getUuid(token, "operatorUsername");
    }

    private UUID getUuidOfAdmin(String token) throws Exception {
        return getUuid(token, "adminUsername");
    }

    // ACTUAL TESTS

    @Test
    @Transactional
    void itShouldPromoteUserToOperator() throws Exception {
        // given
        String token = loginAsAdmin();
        UUID uuid = getUuidOfUser(token);
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/promote/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo(format("The user [%s] has been successfully promoted to operator", uuid));
    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword"
    })
    @Transactional
    void itShouldNotPromoteUserToOperatorNoAdminPrivileges(String loginUsername, String loginPassword) throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(loginUsername, loginPassword);
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(loginRequest))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String weakToken = loginResponse.token();
        String adminToken = loginAsAdmin();
        UUID uuid = getUuidOfUser(adminToken);
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/promote/%s", uuid))
                        .header("Authorization", format("Bearer %s", weakToken))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @Test
    @Transactional
    void itShouldNotPromoteUserToOperatorImproperUuid() throws Exception {
        // given
        String token = loginAsAdmin();
        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v0.0.1/users/promote/abc")
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isBadRequest());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>() {});
        assertThat(responseBody.get("error")).isEqualTo("Wrong input in the request");
        assertThat(responseBody.get("message")).isEqualTo("Make sure the request is formatted correctly");
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.BAD_REQUEST.toString());
    }

    @Test
    @Transactional
    void itShouldNotPromoteUserToOperatorUserDoesNotExist() throws Exception {
        // given
        String token = loginAsAdmin();
        UUID uuid = UUID.randomUUID();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/promote/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String content = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(content, new TypeReference<>() {});
        assertThat(responseBody.get("error")).isEqualTo("Cannot promote user");
        assertThat(responseBody.get("message")).isEqualTo(format("Could not find user with uuid [%s] to be promoted to Operator", uuid));
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername",
            "adminUsername"
    })
    @Transactional
    void itShouldNotPromoteUserToOperatorUserIsNotARoleUser(String username) throws Exception {
        // given
        String token = loginAsAdmin();
        ResultActions getUuidResultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
                        .header("Authorization", format("Bearer %s", token))
        );
        getUuidResultActions.andExpect(status().isOk());
        String getUuidResponseBody = getUuidResultActions.andReturn().getResponse().getContentAsString();
        User user = parser.java(getUuidResponseBody, User.class);
        UUID uuid = user.getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/promote/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isConflict());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        assertThat(responseBody.get("error")).isEqualTo("Cannot promote user");
        assertThat(responseBody.get("message")).isEqualTo(format("Could not promote user [%s] because they do not have USER role", uuid));
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.CONFLICT.toString());
    }

    // TODO fill in these tests to finish adminAPI v0.0.1

    @Test
    @Transactional
    void itShouldBanUser() {
        // given
        // when
        // then
    }

    @Test
    @Transactional
    void itShouldNotBanUserNoAdminPrivileges() {
        // given
        // when
        // then
    }

    @Test
    @Transactional
    void itShouldNotBanUserDoesNotExist() {
        // given
        // when
        // then
    }

    @Test
    @Transactional
    void itShouldNotBanUserIsAnAdmin() {
        // given
        // when
        // then
    }

    @Test
    @Transactional
    void itShouldNotBanUserTheyAreAlreadyBanned() {
        // given
        // when
        // then
    }

    @Test
    @Transactional
    void itShouldUnbanUser() {
        // given
        // when
        // then
    }

    @Test
    @Transactional
    void itShouldNotUnbanUserNoAdminPrivileges() {
        // given
        // when
        // then
    }

    @Test
    @Transactional
    void itShouldNotUnbanUserDoesNotExist() {
        // given
        // when
        // then
    }

    @Test
    @Transactional
    void itShouldNotUnbanUserTheyAreNotBannedInTheFirstPlace() {
        // given
        // when
        // then
    }

    @Test
    @Transactional
    void itShouldDemoteUser() throws Exception {
        // given
        String token = loginAsAdmin();
        UUID uuid = getUuidOfOperator(token);
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/demote/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo(format("The user [%s] has been demoted to a plain user", uuid));
    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword"
    })
    @Transactional
    void itShouldNotDemoteUserNoAdminPrivileges(String loginUsername, String loginPassword) throws Exception {
        // given
        ResultActions loginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(
                                new LoginRequest(loginUsername, loginPassword)
                        ))
        );
        loginResultActions.andExpect(status().isOk());
        String loginResponseBody = loginResultActions.andReturn().getResponse().getContentAsString();
        AuthenticationResponse loginResponse = parser.java(loginResponseBody, AuthenticationResponse.class);
        String weakToken = loginResponse.token();
        String adminToken = loginAsAdmin();
        UUID uuid = getUuidOfUser(adminToken);
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/demote/%s", uuid))
                        .header("Authorization", format("Bearer %s", weakToken))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @Test
    @Transactional
    void itShouldNotDemoteUserDoesNotExist() throws Exception {
        // given
        String token = loginAsAdmin();
        UUID uuid = UUID.randomUUID();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/demote/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        assertThat(responseBody.get("error")).isEqualTo("Cannot demote user");
        assertThat(responseBody.get("message")).isEqualTo(format("Could not find user with uuid [%s] to be demoted to a plain user", uuid));
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());

    }

    @ParameterizedTest
    @CsvSource({
            "userUsername",
            "adminUsername"
    })
    @Transactional
    void itShouldNotDemoteUserIsNotOperator(String username) throws Exception {
        // given
        String token = loginAsAdmin();
        ResultActions getUuidResultActions = mockMvc.perform(
                get(format("/api/v0.0.1/users/username/%s", username))
                        .header("Authorization", format("Bearer %s", token))
        );
        getUuidResultActions.andExpect(status().isOk());
        String getUuidResponseBody = getUuidResultActions.andReturn().getResponse().getContentAsString();
        User user = parser.java(getUuidResponseBody, User.class);
        UUID uuid = user.getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/demote/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isConflict());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        assertThat(responseBody.get("error")).isEqualTo("Cannot demote user");
        assertThat(responseBody.get("message")).isEqualTo(format("Could not demote user [%s] because they are not an operator", uuid));
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.CONFLICT.toString());
    }
}
