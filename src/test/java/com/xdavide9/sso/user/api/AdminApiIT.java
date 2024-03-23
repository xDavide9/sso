package com.xdavide9.sso.user.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xdavide9.sso.authentication.LoginRequest;
import com.xdavide9.sso.common.config.TestingDatabaseConfig;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Tests the integration between AdminController, AdminService and UserRepository

// All tests should be wrapped in transactions because they modify database state
// and might lead to test failure elsewhere

// using test component TestAuthenticator because authentication features are tested else where

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestAuthenticator.class, TestingDatabaseConfig.class})
public class AdminApiIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonParserService parser;
    @Autowired
    private TestAuthenticator authenticator;

    @Test
    @Transactional
    void itShouldPromoteUserToOperator() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/promote/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo(format("The user [%s] has been successfully promoted to operator", uuid));
        // change should be recorded
        // change should be recorded
        ResultActions getChanges = mockMvc.perform(
                get("/api/v0.0.1/users/changes")
                        .header("Authorization", format("Bearer %s", token))
        );
        String json = getChanges.andReturn().getResponse().getContentAsString();
        List<UserChange> changes = parser.java(json, new TypeReference<>(){});
        UserChange userChange = changes.get(0);
        assertThat(userChange.getField()).isEqualTo(UserField.ROLE);
        assertThat(userChange.getPreviousValue()).isEqualTo("USER");
        assertThat(userChange.getUpdatedValue()).isEqualTo("OPERATOR");
    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword"
    })
    @Transactional
    void itShouldNotPromoteUserToOperatorNoAdminPrivileges(String loginUsername, String loginPassword) throws Exception {
        // given
        String weakToken = authenticator.login(loginUsername, loginPassword);
        String adminToken = authenticator.loginAsAdmin();
        UUID uuid = authenticator.getUserWithRoleUser(adminToken).getUuid();
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
        String token = authenticator.loginAsAdmin();
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
        String token = authenticator.loginAsAdmin();
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
        String token = authenticator.loginAsAdmin();
        User user = authenticator.getGenericUser(token, username);
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

    @Test
    @Transactional
    void itShouldBanUser() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();
        // this token is going to become useless once the operator is banned
        String operatorToken = authenticator.loginAsOperator();
        UUID operatorUuid = authenticator.getUserWithRoleOperator(token).getUuid();
        // when
        ResultActions operatorBanResultActions = mockMvc.perform(
                delete(format("/api/v0.0.1/users/ban/%s", operatorUuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        operatorBanResultActions.andExpect(status().isOk());
        String operatorBanResponseBody = operatorBanResultActions.andReturn().getResponse().getContentAsString();
        assertThat(operatorBanResponseBody).isEqualTo(format("The user [%s] has been successfully banned from the system", operatorUuid));
        // banned operator cannot log in and get new tokens
        ResultActions cannotLoginResultActions = mockMvc.perform(
                post("/api/v0.0.1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parser.json(new LoginRequest("operatorUsername", "operatorPassword")))
        );
        cannotLoginResultActions.andExpect(status().isForbidden());
        String contentAsString = cannotLoginResultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        assertThat(responseBody.get("error")).isEqualTo("Login request into banned account");
        assertThat(responseBody.get("message")).isEqualTo("The account with subject [operatorUsername] is banned.");
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.FORBIDDEN.toString());
        // banned operator cannot perform requests even if they are already logged in and have a token
        ResultActions resultActions = mockMvc.perform(
                get("/api/v0.0.1/users/username/userUsername")
                        .header("Authorization", format("Bearer %s", operatorToken))
        );
        resultActions.andExpect(status().isForbidden());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo(format("This account [%s] has been disabled by an Admin.", operatorUuid));
        // banned operator change should be recorded in db
        ResultActions getChanges = mockMvc.perform(
                get("/api/v0.0.1/users/changes")
                        .header("Authorization", format("Bearer %s", token))
        );
        getChanges.andExpect(status().isOk());
        String changesString = getChanges.andReturn().getResponse().getContentAsString();
        List<UserChange> userChanges = parser.java(changesString, new TypeReference<>() {});
        UserChange change = userChanges.get(0);
        assertThat(change.getField()).isEqualTo(UserField.ENABLED);
        assertThat(change.getPreviousValue()).isEqualTo("true");
        assertThat(change.getUpdatedValue()).isEqualTo("false");
    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword"
    })
    @Transactional
    void itShouldNotBanUserNoAdminPrivileges(String loginUsername, String loginPassword) throws Exception {
        // given
        String adminToken = authenticator.loginAsAdmin();;
        String weakToken = authenticator.login(loginUsername, loginPassword);
        UUID uuid = authenticator.getUserWithRoleUser(adminToken).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                delete(format("/api/v0.0.1/users/ban/%s", uuid))
                        .header("Authorization", format("Bearer %s", weakToken))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @Test
    @Transactional
    void itShouldNotBanUserDoesNotExist() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();
        UUID uuid = UUID.randomUUID();
        // when
        ResultActions resultActions = mockMvc.perform(
                delete(format("/api/v0.0.1/users/ban/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        assertThat(responseBody.get("error")).isEqualTo("Cannot ban user");
        assertThat(responseBody.get("message")).isEqualTo(format("Could not find user with uuid [%s] to be banned", uuid));
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    @Transactional
    void itShouldNotBanUserIsAnAdmin() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();
        UUID uuid = authenticator.getUserWithRoleAdmin(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                delete(format("/api/v0.0.1/users/ban/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isConflict());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        assertThat(responseBody.get("error")).isEqualTo("Cannot ban user");
        assertThat(responseBody.get("message")).isEqualTo(format("Could not ban user [%s] because they are an admin", uuid));
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.CONFLICT.toString());
    }

    @Test
    @Transactional
    void itShouldNotBanUserTheyAreAlreadyBanned() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();;
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
        ResultActions successResultActions = mockMvc.perform(
                delete(format("/api/v0.0.1/users/ban/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        successResultActions.andExpect(status().isOk());
        // when
        ResultActions resultActions = mockMvc.perform(
                delete(format("/api/v0.0.1/users/ban/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isConflict());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        assertThat(responseBody.get("error")).isEqualTo("Cannot ban user");
        assertThat(responseBody.get("message")).isEqualTo(format("Could not ban user [%s] because they are already banned", uuid));
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.CONFLICT.toString());
    }

    @Test
    @Transactional
    void itShouldUnbanUser() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();
        String operatorToken = authenticator.loginAsOperator();
        UUID uuid = authenticator.getUserWithRoleOperator(token).getUuid();
        ResultActions given = mockMvc.perform(
                delete("/api/v0.0.1/users/ban/" + uuid)
                        .header("Authorization", format("Bearer %s", token))
        );
        given.andExpect(status().isOk());
        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v0.0.1/users/unban/" + uuid)
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo(format("The user [%s] has been successfully unbanned", uuid));
        // unbanned operator can perform requests again
        ResultActions unbannedRequest = mockMvc.perform(
                get("/api/v0.0.1/users/username/userUsername")
                        .header("Authorization", format("Bearer %s", operatorToken))
        );
        unbannedRequest.andExpect(status().isOk());
        // unbanned operator change should be recorded in db
        ResultActions getChanges = mockMvc.perform(
                get("/api/v0.0.1/users/changes")
                        .header("Authorization", format("Bearer %s", token))
        );
        getChanges.andExpect(status().isOk());
        String changesString = getChanges.andReturn().getResponse().getContentAsString();
        List<UserChange> userChanges = parser.java(changesString, new TypeReference<>(){});
        UserChange change = userChanges.get(1);
        assertThat(change.getField()).isEqualTo(UserField.ENABLED);
        assertThat(change.getPreviousValue()).isEqualTo("false");
        assertThat(change.getUpdatedValue()).isEqualTo("true");
    }

    @ParameterizedTest
    @CsvSource({
            "operatorUsername,operatorPassword",
            "userUsername,userPassword"
    })
    @Transactional
    void itShouldNotUnbanUserNoAdminPrivileges(String loginUsername, String loginPassword) throws Exception {
        // given
        String weakToken = authenticator.login(loginUsername, loginPassword);
        String adminToken = authenticator.loginAsAdmin();
        UUID uuid = authenticator.getUserWithRoleUser(adminToken).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/unban/%s", uuid))
                        .header("Authorization", format("Bearer %s", weakToken))
        );
        // then
        resultActions.andExpect(status().isForbidden());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("Access Denied. You do not have enough authorization to access the request resource.");
    }

    @Test
    @Transactional
    void itShouldNotUnbanUserDoesNotExist() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();
        UUID uuid = UUID.randomUUID();
        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v0.0.1/users/unban/" + uuid)
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isNotFound());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        assertThat(responseBody.get("error")).isEqualTo("Cannot unban user");
        assertThat(responseBody.get("message")).isEqualTo(format("Could not find user with uuid [%s] to be unbanned", uuid));
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.NOT_FOUND.toString());
    }

    @Test
    @Transactional
    void itShouldNotUnbanUserTheyAreNotBannedInTheFirstPlace() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();
        UUID uuid = authenticator.getUserWithRoleUser(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v0.0.1/users/unban/" + uuid)
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isConflict());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseBody = parser.java(contentAsString, new TypeReference<>(){});
        assertThat(responseBody.get("error")).isEqualTo("Cannot unban user");
        assertThat(responseBody.get("message")).isEqualTo(format("Could not unban user [%s] because they are not banned", uuid));
        assertThat(responseBody.get("status")).isEqualTo(HttpStatus.CONFLICT.toString());
    }

    @Test
    @Transactional
    void itShouldDemoteUser() throws Exception {
        // given
        String token = authenticator.loginAsAdmin();
        String operatorToken = authenticator.loginAsOperator();
        UUID uuid = authenticator.getUserWithRoleOperator(token).getUuid();
        // when
        ResultActions resultActions = mockMvc.perform(
                put(format("/api/v0.0.1/users/demote/%s", uuid))
                        .header("Authorization", format("Bearer %s", token))
        );
        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseBody).isEqualTo(format("The user [%s] has been demoted to a plain user", uuid));
        // user should now be forbidden from operator requests
        ResultActions getUsers = mockMvc.perform(
                get("/api/v0.0.1/users")
                        .header("Authorization", format("Bearer %s", operatorToken))
        );
        getUsers.andExpect(status().isForbidden());
        // change should be recorded
        ResultActions getChanges = mockMvc.perform(
                get("/api/v0.0.1/users/changes")
                        .header("Authorization", format("Bearer %s", token))
        );
        String json = getChanges.andReturn().getResponse().getContentAsString();
        List<UserChange> changes = parser.java(json, new TypeReference<>(){});
        UserChange userChange = changes.get(0);
        assertThat(userChange.getField()).isEqualTo(UserField.ROLE);
        assertThat(userChange.getPreviousValue()).isEqualTo("OPERATOR");
        assertThat(userChange.getUpdatedValue()).isEqualTo("USER");

    }

    @ParameterizedTest
    @CsvSource({
            "userUsername,userPassword",
            "operatorUsername,operatorPassword"
    })
    @Transactional
    void itShouldNotDemoteUserNoAdminPrivileges(String loginUsername, String loginPassword) throws Exception {
        // given
        String weakToken = authenticator.login(loginUsername, loginPassword);
        String adminToken = authenticator.loginAsAdmin();
        UUID uuid = authenticator.getUserWithRoleOperator(adminToken).getUuid();
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
        String token = authenticator.loginAsAdmin();
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
        String token = authenticator.loginAsAdmin();
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
